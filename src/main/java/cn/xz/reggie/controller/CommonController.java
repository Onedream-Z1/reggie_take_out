package cn.xz.reggie.controller;

import cn.xz.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传下载的controller
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> fileUpload(MultipartFile file){
        log.info("file={}",file.toString());
        log.info("basePath={}",basePath);
        //说明:file其实是一个临时文件，需要转存在指定的位置否则本次请求结束后临时文件会删除

        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名重复造成文件覆盖问题
        String fileName = UUID.randomUUID()+suffix;

        //创建一个目录对象防止文件不存在报错
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        //将临时文件存储到指定位置
        try {
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    /**
     * 文件下载功能
     */
    @GetMapping("/download")
    public void fileDownload(String name, HttpServletResponse response){

        FileInputStream fis=null;
        ServletOutputStream os=null;
        try {
            //输入流，通过输入流读取文件的内容
            fis = new FileInputStream(basePath + name);
            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            os = response.getOutputStream();

            int len=0;
            byte[] bytes = new byte[1024];
            while ((len=fis.read(bytes))!=-1){
                os.write(bytes,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fis!=null && os!=null){
                    fis.close();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
