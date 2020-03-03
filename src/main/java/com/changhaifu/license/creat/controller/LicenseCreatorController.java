package com.changhaifu.license.creat.controller;


import com.changhaifu.license.bean.LicenseCheck;
import com.changhaifu.license.bean.LicenseCreator;
import com.changhaifu.license.bean.LicenseCreatorParam;
import com.changhaifu.license.service.AGxServerInfos;
import com.changhaifu.license.service.LinuxServerInfos;
import com.changhaifu.license.service.WindowsServerInfos;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>用于生成证书文件 == !!!不能放在给客户部署的代码里</p>
 *
 * @author appleyk
 * @version V.1.0.1
 * @blob https://blog.csdn.net/appleyk
 * @date created on 下午 2:21 2019-9-26
 */
@RestController
@RequestMapping("/license")
public class LicenseCreatorController {

    /**
     * 证书生成路径
     */
    @Value("${license.licensePath}")
    private String licensePath;


    /**
     * 证书生成根路径
     */
    @Value("${license.licenseRootPath}")
    private String licenseRootPath;

    /**
     * <p>获取服务器硬件信息</p>
     * @param osName 操作系统类型，如果为空则自动判断
     */
    @RequestMapping(value = "/getServerInfos",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public LicenseCheck getServerInfos(@RequestParam(value = "osName",required = false) String osName) {

        //操作系统类型
        if(StringUtils.isBlank(osName)){
            osName = System.getProperty("os.name");
        }
        osName = osName.toLowerCase();
        AGxServerInfos abstractServerInfos;
        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith("windows")) {
            abstractServerInfos = new WindowsServerInfos();
        } else if (osName.startsWith("linux")) {
            abstractServerInfos = new LinuxServerInfos();
        }else{//其他服务器类型
            abstractServerInfos = new LinuxServerInfos();
        }
        return abstractServerInfos.getServerInfos();

    }

    /**
     * <p>生成证书</p>
     * @param param 生成证书需要的参数，如：{"subject":"ccx-models","privateAlias":"privateKey","keyPass":"5T7Zz5Y0dJFcqTxvzkH5LDGJJSGMzQ","storePass":"3538cef8e7","licensePath":"C:/Users/zifangsky/Desktop/license.lic","privateKeysStorePath":"C:/Users/zifangsky/Desktop/privateKeys.keystore","issuedTime":"2018-04-26 14:48:12","expiryTime":"2018-12-31 00:00:00","consumerType":"User","consumerAmount":1,"description":"这是证书描述信息","licenseCheckModel":{"ipAddress":["192.168.245.1","10.0.5.22"],"macAddress":["00-50-56-C0-00-01","50-7B-9D-F9-18-41"],"cpuSerial":"BFEBFBFF000406E3","mainBoardSerial":"L1HF65E00X9"}}
     */
    @RequestMapping(value = "/generateLicense",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public HttpServletResponse generateLicense(HttpServletResponse response,@RequestBody LicenseCreatorParam param) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        Map<String,Object> resultMap = new HashMap<>(3);
        if(StringUtils.isBlank(param.getLicensePath())){
            param.setLicensePath(licensePath);
        }else {
            param.setLicensePath(licenseRootPath+ File.separator+uuid+".lic");
        }

        LicenseCreator licenseCreator = new LicenseCreator(param);
        boolean result = licenseCreator.generateLicense();

        if(result){
            try {
                File file = new File(param.getLicensePath());
                InputStream fis = new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                // 清空response
                response.reset();
                // 设置response的Header
                response.addHeader("Content-Disposition", "attachment;filename=" +  new String("license.lic".getBytes(),"ISO-8859-1"));
                response.addHeader("Content-Length", "" + file.length());
                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");
                toClient.write(buffer);
                toClient.flush();
                toClient.close();
                //FileUtils.forceDelete(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
           /* resultMap.put("result","ok");
            resultMap.put("msg",param);
            resultMap.put("date",MessageFormat.format("证书安装成功，证书有效期：{0} - {1}",
                    format.format(param.getIssuedTime()),format.format(param.getExpiryTime())));*/
            return response;
        }
        return null;
    }

}