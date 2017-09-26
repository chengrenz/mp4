# cordova-plugin-minimp4
> Smaller storage  than cordova-plugin-media-capture and the Same function

## 写在前面
一个可以控制时间长度的小视频Cordova插件，开发需求若对内存大小敏感，此插件或许有帮助。进行定制化开发后的此插件，以魅蓝3s录制结果为例，在时间长为10s时的视频大小仅仅600+ KB。

## 安装
1. 下载，然后复制所在的磁盘位置
2. 在当前工程根目录下运行以下命令

```
cordova plugin add 粘贴【下载的磁盘位置】
``` 

## 移除

```
cordova plugin rm com.demo.PluginMiniMp4
```

## 调用

```
$scope.diaoYong = function (user) { //在angular代码中调用
     window.plugins.PluginMiniMp4.showmsg("12", success, error); // 第一个参数为录制视频的时长，后面两个参数是回掉函数
}

var success = function (success) {//成功回调函数
  alert(success); // success是成功时传回的参数，即小视频的URI
};

var error = function (fail) {//失败回调函数
  alert(fail); //fail是成功时传回的参数，不用管他
};

```

## Demo
[http://www.markcoding.cn/demo.gif](http://www.markcoding.cn/demo.gif)


## 注意的地方：
1. 在添加插件成功后，使用`cordova build android`或者使用WebStorm打包时，可能会出现乱码的情况，此时可以参考[此链接](http://blog.csdn.net/u011054333/article/details/54175641)解决
2. 有问题可以联系邮箱`coderlius@qq.com`
3. **欢迎star,issue**
