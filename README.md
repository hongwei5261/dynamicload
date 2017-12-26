#项目简介
app为插件的外壳
plugin为具体的插件内容

#执行步骤
1.运行插件plugin，生成plugin-debug.apk
2.将生成的plugin-debug.apk push到指定的路径下，adb push pluginDemo/build/outputs/apk/debug/pluginDemo-debug.apk /data/data/com.example.weihong.dynamicloadbyclassloader/cache/
3.运行外壳app，具体效果如下

序列图
```sequence
App->App: 1.读取插件apk包信息
App->App: 2.加载插件apk或dex
App->Plugin: 3.反射调用插件Activity，传入代理Activity上下文
Plugin-->Plugin:4.通过传入的代理proxyActivity，执行操作
```

需解决问题
加载class文件
加载资源
activity生命周期

参考
http://blog.csdn.net/singwhatiwanna/article/details/22597587