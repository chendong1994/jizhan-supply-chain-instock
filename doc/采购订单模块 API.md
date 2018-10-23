### 新增采购订单
```
POST /purchase/order/create
```

参数
```
providerId:15
loanDate:100
items:[{productId: 8507,productQuantity:2},{productId: 8506,productQuantity:2}]
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "orderId": "1534909599518638227",
        "orderStatus": 0,
        "orderAmount": 1781.76,
        "loanDate": 100,
        "payStatus": 0,
        "payDate": 1543549601158,
        "providerId": 15,
        "providerName": "集栈供应链"
    }
}
```


### 查询采购订单列表

```
GET /purchase/order/list
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
                "orderId": "1534833013751741866",
                "orderStatus": 0,
                "orderAmount": 1777.76,
                "loanDate": 100,
                "payStatus": 0,
                "payDate": "2018-08-21 14:30:16",
                "providerId": 15,
                "providerName": "集栈供应链"
            },
            {
                "orderId": "1534833136407604817",
                "orderStatus": 0,
                "orderAmount": 1777.76,
                "loanDate": 100,
                "payStatus": 0,
                "payDate": "2018-08-21 14:32:19",
                "providerId": 15,
                "providerName": "集栈供应链"
            },
            {
                "orderId": "1534833190882581675",
                "orderStatus": 0,
                "orderAmount": 1781.76,
                "loanDate": 100,
                "payStatus": 0,
                "payDate": "2018-08-21 14:33:15",
                "providerId": 15,
                "providerName": "集栈供应链"
            },
            {
                "orderId": "1534909599518638227",
                "orderStatus": 0,
                "orderAmount": 1781.76,
                "loanDate": 100,
                "payStatus": 0,
                "payDate": "2018-11-30 11:46:41",
                "providerId": 15,
                "providerName": "集栈供应链"
            }
        ],
        "totalPage": 1
    }
}
```


### 根据订单号或者供应商名字查询
```
GET /purchase/order/findByOrderIdOrProviderName
```

参数
```
orderId:1534833136407604817
//providerName:杨贤达
page:20
size:10
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "data": {
            "orderId": "1534833136407604817",
            "orderStatus": 0,
            "orderAmount": 1777.76,
            "loanDate": 100,
            "payStatus": 0,
            "payDate": "2018-08-21 14:32:19",
            "providerId": 15,
            "providerName": "集栈供应链"
        },
        "totalPage": 1
    }
}
```

### 查询订单详情
```
GET /purchase/order/detail
```

参数
```
orderId:1534833190882581675
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": [
        {
            "detailId": "1534833190884516678",
            "orderId": "1534833190882581675",
            "productId": 42,
            "productName": "kracie/肌美精 3D立体面膜 粉色 超浸透舒缓护肤滋润 30毫升*4片",
            "productJancode": "4901417630674",
            "productPrice": 888.88,
            "productQuantity": 2
        },
        {
            "detailId": "1534833193855739075",
            "orderId": "1534833190882581675",
            "productId": 43,
            "productName": "kracie/肌美精 3D立体面膜 蓝色 提亮肤色超浸透 30ml/片 4片装",
            "productJancode": "4901417631381",
            "productPrice": 2,
            "productQuantity": 2
        }
    ]
}
```

### 取消订单

```
GET /purchase/order/cancel
```

参数
```
orderId:1534909599518638227
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```

### 订单付款

```
GET /purchase/order/paid
```

参数
```
orderId:1534833190882581675
realAmount:888
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```


### 按照商品名称或者 JANCODE 查询供应商能提供的商品列表

```
GET /purchase/order/findByNameOrJan
```

参数
```
param:gfd -> 这个参数为商品名称或者 jancode
providerId:15
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": [
        {
            "productId": 8506,
            "productJancode": "3333",
            "productName": "gfdsa"
        }
    ]
}
```


### 根据订单编号查询采购订单详情
```
GET /purchase/order/findOrderById
```

参数
```
orderId:1534909599518638227
page:1
size:10
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "data": [
            {
                "detailId": "1534909599522770816",
                "orderId": "1534909599518638227",
                "productId": 8507,
                "productName": "PERFECT ONE帕妃雯全效保湿拉提胶原多效面霜 20g",
                "productJancode": "4512274006269",
                "productPrice": 888.88,
                "productQuantity": 2
            },
            {
                "detailId": "1534909600356302119",
                "orderId": "1534909599518638227",
                "productId": 8506,
                "productName": "PERFECT ONE帕妃雯 6合1胶原蛋白美容液保湿面霜75g",
                "productJancode": "4512274006115",
                "productPrice": 2,
                "productQuantity": 2
            }
        ],
        "totalPage": 1
    }
}
```





### 仓库确定采购订单
```
POST /purchase/order/confirmOrder
```

参数
```
orderId:1534909599518638227
providerId:15
items:[{productId:8506, productQuantity:2},{productId:8507, productQuantity:2}]
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "orderId": "1534909599518638227",
        "orderStatus": 4,
        "orderAmount": 1781.76,
        "realAmount": 1781.76,
        "loanDate": 100,
        "payStatus": 0,
        "payDate": 1543549601000,
        "providerId": 15,
        "providerName": "集栈供应链"
    }
}
```


