## 飞翩 API
### 获取供应商列表

```
GET /product/provider/findAll
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
    "data": [
        {
            "providerId": 1,
            "companyName": "シンセン远高保健品株式会社"
        },
        {
            "providerId": 2,
            "companyName": "シンセン远高保健品株式会社"
        },
        {
            "providerId": 13,
            "companyName": "シンセン远高保健品株式会社"
        },
        {
            "providerId": 14,
            "companyName": "シンセン远高保健品株式会社"
        },
        {
            "providerId": 15,
            "companyName": "シンセン远高保健品株式会社"
        }
    ]
}

```

### 获取供应商列表（分页）

```
GET /product/provider/list
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
                "providerId": 1,
                "companyName": "シンセン远高保健品株式会社",
                "companyAbbr": "远高",
                "companyAddress": "东京都池袋圈",
                "legalPerson": "中本三郎",
                "registeredFund": 10000001,
                "depositBank": "三井银行",
                "bankAccount": "156164896",
                "purchaseContact": "李四",
                "contactPhone": "121156156",
                "loanDate": 200,
                "providerNo": 111,
                "createTime": 1532655419000,
                "updateTime": 1532704368000
            },
            {
                "providerId": 2,
                "companyName": "シンセン远高保健品株式会社",
                "companyAbbr": "远高",
                "companyAddress": "东京都池袋圈XXXX",
                "legalPerson": "杨贤达",
                "registeredFund": 100000012,
                "depositBank": "中国银行",
                "bankAccount": "123456789",
                "purchaseContact": "1086",
                "contactPhone": "123456789",
                "loanDate": 1000,
                "providerNo": 688,
                "createTime": 1532672687000,
                "updateTime": 1533477083000
            },
            {
                "providerId": 13,
                "companyName": "シンセン远高保健品株式会社",
                "companyAbbr": "远高",
                "companyAddress": "东京都池袋圈XXXX",
                "legalPerson": "杨贤达",
                "registeredFund": 100000013,
                "depositBank": "中国银行",
                "bankAccount": "123456789",
                "purchaseContact": "1086",
                "contactPhone": "123456789",
                "loanDate": 1000,
                "providerNo": 264,
                "createTime": 1532672687000,
                "updateTime": 1532672948000
            },
            {
                "providerId": 14,
                "companyName": "シンセン远高保健品株式会社",
                "companyAbbr": "远高",
                "companyAddress": "东京都池袋圈XXXX",
                "legalPerson": "杨贤达",
                "registeredFund": 100000014,
                "depositBank": "中国银行",
                "bankAccount": "123456789",
                "purchaseContact": "1086",
                "contactPhone": "123456789",
                "loanDate": 1000,
                "providerNo": 418,
                "createTime": 1532672687000,
                "updateTime": 1532672948000
            },
            {
                "providerId": 15,
                "companyName": "シンセン远高保健品株式会社",
                "companyAbbr": "远高",
                "companyAddress": "东京都池袋圈XXXX",
                "legalPerson": "杨贤达",
                "registeredFund": 100000015,
                "depositBank": "中国银行",
                "bankAccount": "123456789",
                "purchaseContact": "1086",
                "contactPhone": "123456789",
                "loanDate": 1000,
                "providerNo": 950,
                "createTime": 1532672687000,
                "updateTime": 1532672948000
            },
            {
                "providerId": 16,
                "companyName": "シンセン远高保健品株式会社",
                "companyAbbr": "远高",
                "companyAddress": "东京都池袋圈XXXX",
                "legalPerson": "杨贤达",
                "registeredFund": 100000016,
                "depositBank": "中国银行",
                "bankAccount": "123456789",
                "purchaseContact": "1086",
                "contactPhone": "123456789",
                "loanDate": 1000,
                "providerNo": 841,
                "createTime": 1532672687000,
                "updateTime": 1532672948000
            },
            {
                "providerId": 17,
                "companyName": "シンセン远高保健品株式会社",
                "companyAbbr": "远高",
                "companyAddress": "东京都池袋圈XXXX",
                "legalPerson": "杨贤达",
                "registeredFund": 100000017,
                "depositBank": "中国银行",
                "bankAccount": "123456789",
                "purchaseContact": "1086",
                "contactPhone": "123456789",
                "loanDate": 1000,
                "providerNo": 300,
                "createTime": 1532672687000,
                "updateTime": 1532672948000
            },
            {
                "providerId": 18,
                "companyName": "シンセン远高保健品株式会社",
                "companyAbbr": "远高",
                "companyAddress": "东京都池袋圈XXXX",
                "legalPerson": "杨贤达",
                "registeredFund": 100000018,
                "depositBank": "中国银行",
                "bankAccount": "123456789",
                "purchaseContact": "1086",
                "contactPhone": "123456789",
                "loanDate": 1000,
                "providerNo": 173,
                "createTime": 1532672687000,
                "updateTime": 1532672948000
            },
            {
                "providerId": 19,
                "companyName": "シンセン远高保健品株式会社",
                "companyAbbr": "远高",
                "companyAddress": "东京都池袋圈XXXX",
                "legalPerson": "杨贤达",
                "registeredFund": 100000019,
                "depositBank": "中国银行",
                "bankAccount": "123456789",
                "purchaseContact": "1086",
                "contactPhone": "123456789",
                "loanDate": 1000,
                "providerNo": 594,
                "createTime": 1532672687000,
                "updateTime": 1532672948000
            },
            {
                "providerId": 20,
                "companyName": "シンセン远高保健品株式会社",
                "companyAbbr": "远高",
                "companyAddress": "东京都池袋圈XXXX",
                "legalPerson": "杨贤达",
                "registeredFund": 100000020,
                "depositBank": "中国银行",
                "bankAccount": "123456789",
                "purchaseContact": "1086",
                "contactPhone": "123456789",
                "loanDate": 1000,
                "providerNo": 335,
                "createTime": 1532672688000,
                "updateTime": 1532672948000
            }
        ],
        "totalPage": 4
    }
}
```

### 新增供应商

```
POST /product/provider/save
```

参数
```
companyName:
companyAbbr:
companyAddress:
legalPerson:
registeredFund:
depositBank:
bankAccount:
purchaseContact:
contactPhone:
loanDate:
providerNo:
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```

### 删除供应商

```
GET /product/provider/delete
```

参数
```
providerId: 
```

返回

```
{
    "code": 0,
    "msg": "成功"
}
```

### 按照供应商 id 查询商品库

```
GET /product/provider/findByProviderId
```

参数
```
providerId:
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": [
        {
            "id": 1,
            "providerId": 1,
            "productId": 1
        },
        {
            "id": 3,
            "providerId": 1,
            "productId": 3
        }
    ]
}
```