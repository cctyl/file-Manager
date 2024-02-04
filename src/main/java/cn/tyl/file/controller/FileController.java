package cn.tyl.file.controller;

import cn.tyl.file.commons.FileInfo;
import cn.tyl.file.commons.R;
import cn.tyl.file.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
public class FileController {

    @Value("${file.path}")
    String basePath;


    @GetMapping("/createdir")
    public R createDirectory(@RequestParam(value = "parentPath", required = false,defaultValue = "") String parentPath,
                             @RequestParam(value = "dirName") String dirName){

        if (!StringUtils.isEmpty(dirName)&& !dirName.equals("undefined")){



            String dirPath = basePath+"/"+parentPath+"/"+dirName;
            File directory = new File(dirPath);
            //不存在就创建，存在就提示
            if (!directory.exists()){
                directory.mkdirs();
            }else {
                return R.error().message("文件夹已经存在");
            }
            return R.ok().message("文件夹："+dirName+"创建成功");

        }else {
            return R.error().message("参数有误");
        }

    }


    /**
     * 文件上传
     * @param file
     * @param request
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    public R uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request,
                        @RequestParam(value = "parentPath", required = false) String parentPath) {

        if (parentPath.equals("undefined")){
            parentPath = "";
        }

        File directory = new File(basePath+"/"+parentPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            String path = directory.getPath() + "/" + file.getOriginalFilename();
            file.transferTo(new File(path));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.ok().message("上传成功");
    }


    /**
     * 获取文件列表数据
     *
     * @param parentPath
     * @param childPath
     * @return
     */
    @GetMapping("/file")
    public R fileDownload(
            @RequestParam(value = "parentPath", required = false) String parentPath,
            @RequestParam(value = "childPath", required = false) String childPath) {


        if (StringUtils.isEmpty(parentPath)||parentPath.equals("undefined")) {
            parentPath = "";
        }


        if (!StringUtils.isEmpty(childPath)) {
            parentPath += "/" + childPath + "/";
        }
        String inputPath = basePath + parentPath;


        File file = new File(inputPath);      //获取其file对象
        File[] fs = file.listFiles();     //遍历path下的文件和目录，放在File数组中

        ArrayList<FileInfo> files = new ArrayList<>();
        ArrayList<FileInfo> directorys = new ArrayList<>();


        if (fs != null) {
            for (File f : fs) {                //遍历File[]数组
                String fileName = f.getName();  //获取文件和目录名
                if (!f.isDirectory()) {  //另外可用fileName.endsWith("txt")来过滤出以txt结尾的文件
                    //计算文件大小，单位是 M
                    long length = f.length()/(1024*1024);
                    if (length<=0){
                        length=1;
                    }
                    files.add(new FileInfo(fileName, null,length,"文件"));
                } else {
                    directorys.add(new FileInfo(fileName, null,null,"文件夹"));
                }

            }
        } else {
            //没有文件，不遍历
            files = null;
            directorys = null;
        }
        return R.ok().data("inputPath", inputPath)
                .data("fileNames", files)
                .data("directoryNames", directorys)
                .data("parentPath", parentPath);
    }

    /**
     * 删除文件
     *
     * @param parentPath
     * @param childPath
     * @return
     */
    @DeleteMapping("/file")
    public R deleteFile(@RequestParam(value = "parentPath", required = false) String parentPath,
                        @RequestParam(value = "childPath", required = false) String childPath) {

        //拼接文件路径
        if (StringUtils.isEmpty(parentPath)) {
            parentPath = "";
        }
        if (!StringUtils.isEmpty(childPath)) {
            parentPath += "/" + childPath + "/";
        }
        String inputPath = basePath + parentPath;

        boolean b = FileUtils.deleteFile(inputPath);
        if (b){
            return R.ok();
        }else {
            return R.error();
        }
    }
    @GetMapping("/download")
    public void downloadFile(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam String downFileName,
                             @RequestParam("parentPath") String parentPath) {
        String filePath = basePath + parentPath;
        File file = new File(filePath + "/" + downFileName);
        /**
         * 中文乱码解决
         */
        String type = request.getHeader("User-Agent").toLowerCase();
        String fileName = null;
        try {
            if (type.indexOf("firefox") > 0 || type.indexOf("chrome") > 0) {
                /**
                 * 谷歌或火狐
                 */
                fileName = new String(file.getName().getBytes(StandardCharsets.UTF_8), "iso8859-1");
            } else {
                /**
                 * IE
                 */
                fileName = URLEncoder.encode(file.getName(), "UTF-8");
            }
            // 设置响应的头部信息
            response.setHeader("content-disposition", "attachment;filename=" + fileName);
            // 设置响应内容的类型
            response.setContentType(fileName + "; charset=UTF-8");
            response.setContentLength((int) file.length());
            // 设置响应内容的长度
            response.setHeader("filename", fileName);
            //通过文件管道获取飞一般的下载速度
            WritableByteChannel writableByteChannel = Channels.newChannel(response.getOutputStream());
            FileChannel fileChannel = new FileInputStream(file.getAbsolutePath()).getChannel();
            fileChannel.transferTo(0, fileChannel.size(), writableByteChannel);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

    }


    /**
     * 重命名文件
     *
     * @param oldName
     * @param parentPath
     * @return
     */
    @GetMapping("/rename")
    public String rename(@RequestParam("oldName") String oldName,
                         @RequestParam(value = "parentPath", required = false) String parentPath,
                         @RequestParam("newName") String newName) {

        if (StringUtils.isEmpty(parentPath)) {
            parentPath = "";
        }
        String filePath = basePath + parentPath;
        File oldfile = new File(filePath + "/" + oldName);
        File newFile = new File(filePath + "/" + newName);
        if (oldfile.exists()) {
            //判断文件父目录是否存在
            oldfile.renameTo(newFile);
        }
        return "redirect:/file?parentPath=" + parentPath;
    }

}
