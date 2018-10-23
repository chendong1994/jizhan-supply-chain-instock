### 地址解析

```
GET address/resolve 
```

参数
```
address: 浙江省绍兴市诸暨市浣东街道西子公寓北区电话：13905857430  衣服  食物 
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "msg": "成功",
        "uid": "333c5ab7e06663598ab95d3e06505fa6e7c02fcd",
        "code": 0,
        "data": [
            {
                "note": "",
                "original": "浙江省绍兴市诸暨市浣东街道西子公寓北区电话：13905857430  衣服  食物",
                "mobile": "13905857430",
                "county_shortname": "诸暨",
                "county_name": "诸暨市",
                "province_name": "浙江省",
                "province_shortname": "浙江",
                "city_name": "绍兴市",
                "county_id": "1357",
                "phone": "",
                "province_id": "334",
                "city_shortname": "绍兴",
                "name": "衣服  食物",
                "detail": "浣东街道西子公寓北区",
                "city_id": "1304"
            }
        ]
    }
}
```





### 6.获取发件人信息列表


```
GET   /addr/findAllByOpenid 
```

参数

```
openid:17

page:1

size:5    //如果不传参默认为5
```

返回

```
{

​	'code':0,

​	'data':[

​		'totalpage':5

​		商品五条数据

​	]

​	'msg':'成功'

}

```

### 7.获取收件人信息列表


```
GET    sender/findAllByOpenid
```

参数
```
openid:17

page:1

size:5    //如果不传参默认为5
```

返回

```
{

​	'code':0,

​	'data':[

​		'totalpage':5

​		商品五条数据

​	]

​	'msg':'成功'

}
```

### 8.删除发件人信息

```
GET    sender/delete
```

参数

```
openid:17

id:10

```

返回

```

{

​	'code':0,

​	'msg':'成功'

}

```



### 9.删除收件人信息


```
GET    addr/delete
```

参数

```
openid:17

id:10
```

返回

```

{

​	'code':0,

​	'msg':'成功'

}

```

### 10.新增收件人信息

```
POST    /addr/save
```

参数
```
openid:17

receiverNickname:'蓝色海洋',

receiver:'刘谦',

phone:'18637018921'

province:'浙江省',

city:'杭州市',

area:'西湖区',

detailAddr:'联合科技广场5-305'

```

返回

```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "id": 1026,
        "openid": "1234567",
        "receiver": "dfghj",
        "receiverNickname": "刘谦",
        "phone": "18637018921",
        "province": "浙江省",
        "city": "杭州市",
        "area": "西湖区",
        "detailAddr": "联合科技广场5-305"
    }
}

```

### 11.新增发件人信息
```
GET    sender/save
```

参数

```

openid:17

sender:'刘谦',

phone:'18637018921'

```

返回

```

{
    "code": 0,
    "msg": "成功",
    "data": {
        "id": 7,
        "openid": "1234567",
        "sender": "刘谦",
        "phone": "13282823673",
        "isDefault": 0
    }
}

```

### 12.修改发件人信息

```
POST    /sender/update
```

参数

```

openid:17,

id:10,

sender:'刘谦',

phone:'18637018921'

```

返回

```

{
    "code": 0,
    "msg": "成功",
    "data": {
        "id": 7,
        "openid": "1234567",
        "sender": "刘谦",
        "phone": "13245672345",
        "isDefault": 0
    }
}

```



### 13.修改收件人信息

```
POST    /addr/update
```

参数

```
openid:17,

id:10

receiverNickname:'蓝色海洋',

receiver:'刘谦',

phone:'18637018921'

province:'浙江省',

city:'杭州市',

area:'西湖区',

detailAddr:'联合科技广场5-305'

```

返回

```

{
    "code": 0,
    "msg": "成功",
    "data": {
        "id": 1026,
        "openid": "1234567",
        "receiver": "刘倩倩",
        "receiverNickname": "刘谦",
        "phone": "18637018921",
        "province": "浙江省",
        "city": "杭州市",
        "area": "西湖区",
        "detailAddr": "联合科技广场5-305"
    }
}

```

### 14.设置为默认发件人信息

```
GET /sender/setDefault
```

参数

```
openid:10,

id:10
```

返回

```
{
    "code": 0,
    "msg": "成功"
}

```


### 新增订单

```
POST /order/save
```

参数
```
{
    "senderAddrId": 111,
    "receiverAddrId": 111,
    "cart": [
            {
                "productId": 11,
                "productQuantity": 2
            },
            {
                "productId": 22,
                "productQuantity": 3
            },
            {
                "productId": 33,
                "productQuantity": 1
            },
            {
                "productId": 44,
                "productQuantity": 4
            }
        ]
}
```

返回

```
{
    "code": 0,
    "msg": "成功",
    "data":
}
```