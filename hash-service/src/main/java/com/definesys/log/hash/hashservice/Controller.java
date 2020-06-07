package com.definesys.log.hash.hashservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/2 12:20
 * @description：
 * @modified By：
 * @version: 1.0
 */
@RestController
@RequestMapping("/hash")
public class Controller {

        @GetMapping("/getHashValue")
        public String getHashValue(String key,String max) throws IOException, InterruptedException {
            Process exec = Runtime.getRuntime().exec("/data/golang/hash.sh "+key+" "+max);
            int status = exec.waitFor();
            if (status != 0) {
                return "脚本调用失败 ";
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            StringBuilder res = new StringBuilder();
            String line;
            while ((line = br.readLine())!= null) {
                System.out.println(line);
                res.append(line).append("\n");
            }

            return res.toString();
        }

}
