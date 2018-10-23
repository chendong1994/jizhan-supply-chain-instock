### 点击 input 框查看历史记录

```
GET /express/historyRecord
```

参数
```
openid:123456
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": [
        "9721821612782",
        "9726325388582"
    ]
}
```

### 查询单号

```
GET /express/query
```

参数
```
openid:123456
expNum:9721821612782
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "traces": [
            {
                "acceptTime": "2018-08-11 17:21:00",
                "acceptAddress": "杭州市",
                "remark": "杭州市邮政速递物流公司滨江区彩虹城揽投站已收件（揽投员姓名：张道言,联系电话:13357158793）"
            },
            {
                "acceptTime": "2018-08-11 18:00:03",
                "acceptAddress": "杭州市",
                "remark": "已离开杭州市邮政速递物流公司滨江区彩虹城揽投站，发往杭州处理中心"
            },
            {
                "acceptTime": "2018-08-11 19:47:12",
                "acceptAddress": "杭州市",
                "remark": "到达  杭州处理中心 处理中心"
            },
            {
                "acceptTime": "2018-08-12 05:39:16",
                "acceptAddress": "杭州市",
                "remark": "离开杭州处理中心 发往杭州市邮政速递物流公司高新区分公司古翠路揽投站"
            },
            {
                "acceptTime": "2018-08-12 08:11:17",
                "acceptAddress": "杭州市",
                "remark": "到达杭州市邮政速递物流公司高新区分公司古翠路揽投站"
            },
            {
                "acceptTime": "2018-08-12 08:29:34",
                "acceptAddress": "杭州市",
                "remark": "杭州市邮政速递物流公司高新区分公司古翠路揽投站安排投递，预计13:00:00前投递（投递员姓名：潘大成;联系电话：18324449549）"
            },
            {
                "acceptTime": "2018-08-12 10:54:23",
                "acceptAddress": "杭州市",
                "remark": "投递并签收，签收人：他人收 北五区12号邮政代收"
            }
        ]
    }
}
```

### 首页下方的查询历史记录预览

```
GET /express/historyRecordPreview
```

参数
```
openid:123456
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": [
        {
            "expNum": "1051875088728",
            "expStatus": 0,
            "queryDate": "2018-08-25 15:36:18"
        },
        {
            "expNum": "9721821612782",
            "expLastRecord": {
                "acceptTime": "2018-08-12 10:54:23",
                "acceptAddress": "杭州市",
                "remark": "投递并签收，签收人：他人收 北五区12号邮政代收"
            },
            "expStatus": 0,
            "queryDate": "2018-08-25 12:06:17"
        },
        {
            "expNum": "9726325388582",
            "expLastRecord": {
                "acceptTime": "2018-08-17 12:28:18",
                "acceptAddress": "杭州市",
                "remark": "投递并签收，签收人：他人收 北五区12号小兵驿站"
            },
            "expStatus": 0,
            "queryDate": "2018-08-25 12:04:56"
        }
    ]
}
```

### 确认物流信息

```
GET /cert/getUserInfo
```

参数
```
expressNum: BS057110025CN
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "recipient": "**婷",
        "mobile": "137****9928",
        "addr": "北京市北京市****0)",
        "expressNum": "BS057110025CN"
    }
}
```

### 身份证正反面, 分两次上传, 请求两次
```
POST /common/upload
```

参数
```
file: Multipart类型
```

返回
```
{
code: 0,
msg: "成功",
data: "https://jizhangyl.oss-cn-shanghai.aliyuncs.com/223dbeab0b8b488383fe1654742003ae.png"
}
```

### 提交证件信息

```
POST /cert/uploadCert
```

参数
```
recipient:杨贤达
openid:123456
name:杨贤达
idNum:340826199412160315
cardFrontUrl:http://1234.jpg
cardRearUrl:http://1234.jpg
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```


### 后台获取待审核列表

```
GET /cert/checkCert
```

参数
```
page: 1
size: 10
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "data": [
            {
                "id": 1,
                "name": "杨贤达",
                "idNum": "340826199412160315",
                "openid": "123456",
                "checkStatus": 0,
                "cardFront": "http://1234.jpg",
                "cardRear": "http://1234.jpg"
            },
            {
                "id": 2,
                "name": "杨贤达",
                "idNum": "340826199412160315",
                "openid": "123456",
                "checkStatus": 0,
                "cardFront": "http://1234.jpg",
                "cardRear": "http://1234.jpg"
            }
        ],
        "totalPage": 1
    }
}
```

### 审核证件信息

```
GET /cert/confirm
```

参数
```
id: 1
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```

### 查询地址列表

```
GET /cert/list
```

参数
```
openid: 123456
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": [
        {
            "id": 1,
            "name": "杨贤达",
            "idNum": "340826199412160315",
            "openid": "123456",
            "checkStatus": 1,
            "cardFront": "http://1234.jpg",
            "cardRear": "http://1234.jpg",
            "expressNum": "1051875088727",
            "saveStatus": 1
        },
        {
            "id": 4,
            "name": "杨贤达",
            "idNum": "340826199412160316",
            "openid": "123456",
            "checkStatus": 0,
            "cardFront": "http://1234.jpg",
            "cardRear": "http://1234.jpg",
            "expressNum": "",
            "saveStatus": 1
        }
    ]
}
```

### 删除列表地址

```
GET /cert/delete
```

参数
```
id: 
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```