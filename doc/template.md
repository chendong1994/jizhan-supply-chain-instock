### 代购订单导入模板上传

```
POST /template/uploadBuyer
```

参数
```
name: template （文件）
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```

### 仓库发货模板上传

```
POST /template/uploadDelivery
```

参数
```
name: template （文件）
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```


### 代购订单导入模板下载

```
GET /template/downloadBuyer
```

参数
```
无
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": "https://jizhangyl.oss-cn-shanghai.aliyuncs.com/5b553bd97bb24ab99bcc68a726e24cd7.xls"
}
```

### 仓库发货模板下载

```
GET /template/downloadDelivery
```

参数
```
无
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": "https://jizhangyl.oss-cn-shanghai.aliyuncs.com/5b553bd97bb24ab99bcc68a726e24cd7.xls"
}
```