应用上架检测合规性
检测用户在同意授权前是否有获取隐私信息的Xposed插件
首先安装xpose 目前xpose仅支持64位app 

安装xposed installer 容器

使用方法
1 修改源码中的hook的APP包名
2 使用as编译
3 安装 
4 在xpose中添加该app的分身, 然后在模块中选中该检测模块,然后xpose 设置中重启配置
5 在xpose 中安装需要检测的app分身,然后打开, 到xposed installer 日志中查看日志输出,是否调用