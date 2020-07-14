package cn.tyl.file.controller;

import cn.tyl.file.commons.FileInfo;
import cn.tyl.file.commons.R;
import cn.tyl.file.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;

@RestController
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

        ArrayList files = new ArrayList();
        ArrayList directorys = new ArrayList();





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

    /**
     * 下载文件
     *
     * @param downFileName
     * @param parentPath
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/download")
    public void downLoad(@RequestParam String downFileName,
                           @RequestParam("parentPath") String parentPath,
                           HttpServletResponse response)
            throws UnsupportedEncodingException {

        String filename = downFileName;
        String filePath = basePath + parentPath;
        File file = new File(filePath + "/" + filename);
        if (file.exists()) {
            //判断文件父目录是否存在

            //设置响应头
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;fileName=" + java.net.URLEncoder.encode(filename, "UTF-8"));

            //流的对拷
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
            OutputStream os = null; //输出流
            try {
                //获取一个对外的输出流
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer);
                    i = bis.read(buffer);
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("----------file download---" + filename);

            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    /**
     * 重命名文件
     *
     * @param oldName
     * @param parentPath
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/rename")
    public String rename(@RequestParam("oldName") String oldName,
                         @RequestParam(value = "parentPath", required = false) String parentPath,
                         @RequestParam("newName") String newName,
                         HttpServletResponse response)
            throws UnsupportedEncodingException {

        if (StringUtils.isEmpty(parentPath)) {
            parentPath = "";
        }
        String filename = oldName;
        String filePath = basePath + parentPath;
        File oldfile = new File(filePath + "/" + filename);
        File newFile = new File(filePath + "/" + newName);
        if (oldfile.exists()) {
            //判断文件父目录是否存在

            oldfile.renameTo(newFile);
        }

        return "redirect:/file?parentPath=" + parentPath;
    }

}
