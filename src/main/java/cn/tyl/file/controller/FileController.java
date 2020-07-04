package cn.tyl.file.controller;

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

@Controller
public class FileController {

    @Value("${file.path}")
    String basePath;

    @PostMapping("/upload")
    @ResponseBody
    public Object uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {


        File directory = new File(basePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            String path = directory.getPath() + "/" + file.getOriginalFilename();
            file.transferTo(new File(path));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "上传成功";
    }


    /*
     *实现文件下载
     */
    @RequestMapping("/file")
    public String fileDownload(Model model,
                               @RequestParam(value = "parentPath", required = false) String parentPath,
                               @RequestParam(value = "childPath", required = false) String childPath) {


        if (StringUtils.isEmpty(parentPath)){
            parentPath="";
        }


        if (!StringUtils.isEmpty(childPath)) {
            parentPath +=  childPath+"/";
        }
        String inputPath =basePath+ parentPath;


        File file = new File(inputPath);      //获取其file对象
        File[] fs = file.listFiles();     //遍历path下的文件和目录，放在File数组中

        ArrayList files = new ArrayList();
        ArrayList directorys = new ArrayList();

        if (fs != null) {
            for (File f : fs) {                //遍历File[]数组
                String fileName = f.getName();  //获取文件和目录名
                if (!f.isDirectory()) {  //另外可用fileName.endsWith("txt")来过滤出以txt结尾的文件

                    files.add(fileName);
                } else {
                    directorys.add(fileName);
                }

            }
        } else {
            //没有文件，不遍历
            files = null;
            directorys = null;
        }


        model.addAttribute("inputPath", inputPath);
        model.addAttribute("fileNames", files);
        model.addAttribute("directoryNames", directorys);
        model.addAttribute("parentPath",parentPath);

        return "download";
    }


    @GetMapping("/download/{downFileName}")
    public String downLoad(@PathVariable String downFileName,
                           @RequestParam("parentPath") String parentPath,
                           HttpServletResponse response)
            throws UnsupportedEncodingException {

        String filename = downFileName;
        String filePath = basePath+parentPath;
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
        return null;
    }
}
