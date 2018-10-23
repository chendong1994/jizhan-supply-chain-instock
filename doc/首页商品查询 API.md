### 按照商品名称模糊查询

```
GET /shop/findByName
```

参数
```
name:化妆水
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": [
        {
            "id": 8446,
            "shop_name": "MUJI/无印良品 敏感肌化妆水 高保湿型 200ml",
            "shop_gprice": "0.00",
            "bc_cprice": "0.00",
            "shop_status": 0
        },
        {
            "id": 8448,
            "shop_name": "MUJI/无印良品 舒柔化妆水 敏感肌清爽水润型 200ml",
            "shop_gprice": "0.00",
            "bc_cprice": "0.00",
            "shop_status": 0
        },
        {
            "id": 8450,
            "shop_name": "MUJI/无印良品 舒柔化妆水 敏感肌滋润舒缓型 200ml",
            "shop_gprice": "0.00",
            "bc_cprice": "0.00",
            "shop_status": 0
        },
        {
            "id": 8437,
            "shop_name": "CPB 肌肤之钥 水磨精华 化妆水 滋润舒缓 170ml",
            "shop_gprice": "486.00",
            "bc_cprice": "54.43",
            "shop_status": 0
        },
        {
            "id": 8406,
            "shop_name": "Dr.Ci.Labo/城野医生 卓效收敛化妆水  控油去黑头保湿 100ml",
            "shop_gprice": "62.00",
            "bc_cprice": "6.94",
            "shop_status": 0
        },
        {
            "id": 8401,
            "shop_name": "SANA/莎娜 豆乳化妆水 美肌白皙滋润保湿 200ml",
            "shop_gprice": "35.32",
            "bc_cprice": "3.96",
            "shop_status": 0
        }
    ]
}
```