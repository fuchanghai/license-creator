  
1.单机模式
用狗 即加密狗
手持u盘，盘里面是证书，验证通过才能操作


2.java license

一	生成私钥、公钥库
		1、首先要用KeyTool工具来生成私匙库：（-alias别名 –validity 3650表示10年有效） keysize 不加的话 默认2048  会报错
	keytool -genkey -alias privatekey -keysize 1024  -keystore privateKeys.store -validity 3650
	 
	2、然后把私匙库内的公匙导出到一个文件当中：
	keytool -export -alias privatekey -file certfile.cer -keystore privateKeys.store
	 
	3、然后再把这个证书文件导入到公匙库：
	keytool -import -alias publiccert -file certfile.cer -keystore publicCerts.store
	 
	最后生成文件privateKeys.store、publicCerts.store拷贝出来备用。
	
eg：密码一定要6位 并且数字和密码都要有，我去看了一下源码 不满足会爆密码不符合条件
	
二 将公钥放入校验模块中，将私钥放入证书生成模块中


三创建证书
	1获取系统信息
	2，根据获取的信息 生成证书（密码，失效日期，绑定的ip ，mac） 接口返回生成的证书
	
	
四 自定义注解 ，构建拦截器
	1自定义注解
	2，拦截handMapping
	3,将拦截器放入拦截器链中
	4，拦截有自定义注解的接口，看出生成的证书是否可用，（公钥密钥是否能对上，密码对不对，日期是否失效，ip 主板信息是否可以对上）


	




验证模块：
		公钥
		自启动获取本地证书
		拦截器（证书验证）
		自定义注解（在实际项目中 在需要验证的接口上加入此注解）

证书生成模块：
		密钥
		生成证书接口



生成模块 发在自己那，调用之后返回证书文件




