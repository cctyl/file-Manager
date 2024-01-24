### 远程文件管理器使用说明 

​	演示视频：https://www.bilibili.com/video/av371330962

​	使用要求：

​			需要安装jdk8 以上

​	解压之后，打开application.yml

​	修改以下几个地方

 * file:
   	   path: E:/迅雷下载/         #改成自己的路径
 * config:
     uname: admin   #用户名，改成自己的用户名
     upwd: admin    #密码，改成自己安全的密码
     key: wqqliyYQsJD   #key

 * server:
     port: 80		         #访问端口，改一个数字，访问路径是   http://localhost:你的端口/




改好之后，双击	开启.bat  ,稍等10秒，访问 http://localhost:你的端口/
