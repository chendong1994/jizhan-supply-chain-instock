
### 全部商品导出
```
GET /shop/export
```

参数
```
无
```

返回
```
弹框提示文件下载
```

### 商品导入
```
GET /shop/import
```

参数
```
提供的excel文件
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```



### 单独上传商品图片接口（可用于批量导入商品后补图）
参数
```
productId:
productImage -> 为一个 file
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```

### 新增商品

```
POST /shop/save
```

参数
```
shopName
shopCount
shopJan
cateId
brandId
shopXcount
shopFormat
isPaogoods
shopJweight
shopDweight
shopColor
shopWhg
shopVolume
shopGprice
shopLprice
bcPrice
bcCess
wenan -> 可为空，其他必填
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```


### 更新商品

```
POST /shop/update
```

参数
```
id
shopName
shopCount
shopJan
cateId
brandId
shopXcount
shopFormat
isPaogoods
shopJweight
shopDweight
shopColor
shopWhg
shopVolume
shopGprice
shopLprice
bcPrice
bcCess
wenan -> 可为空，其他必填
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```


### 删除商品
```
GET /shop/delete
```

参数
```
id
```
返回
```
{
    "code": 0,
    "msg": "成功"
}
```


### 获取所有商品（包括下架商品）
```
GET /shop/list
```

参数
```
page
size
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "goodList": [
            {
                "id": 18,
                "shop_name": "Santen/参天 FX眼药水 银色 清凉缓解红血丝12ml",
                "shop_image": "/jzgyl/public/upload/20180727/54da509e26d38c6ab985d8ae05a04062.png",
                "shop_gprice": 0,
                "brand_name": "FX/参天",
                "cate_name": "家具日用",
                "bc_price": 0
            },
            {
                "id": 19,
                "shop_name": "GEKKA 睡眠面膜 保湿美白收缩毛孔 80g",
                "shop_image": "/jzgyl/public/upload/20180727/41379e4fdf7d34a93b829bd6c16747ff.png",
                "shop_gprice": 0,
                "brand_name": "GEKKA",
                "cate_name": "面膜",
                "bc_price": 0
            },
            {
                "id": 20,
                "shop_name": "HABA 鲨烷精纯油 白色 美容 15ml",
                "shop_image": "/jzgyl/public/upload/20180727/8bc8ebc83b2e1dc6929590f8502aefe9.png",
                "shop_gprice": 0,
                "brand_name": "HABA",
                "cate_name": "护肤",
                "bc_price": 0
            },
            {
                "id": 23,
                "shop_name": "Naturie/娥佩兰 薏仁化妆水 美白保湿防晒 500ml",
                "shop_image": "/jzgyl/public/upload/20180727/5b9035760b188335ec4300a1079890f4.png",
                "shop_gprice": 0,
                "brand_name": "Naturie/娥佩兰",
                "cate_name": "护肤",
                "bc_price": 0
            }
        ],
        "totalpage": 4
    }
}
```
### 查询上架
```
GET /shop/findUp
```

参数
```
page
size
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "goodList": [
            {
                "id": 18,
                "shop_name": "Santen/参天 FX眼药水 银色 清凉缓解红血丝12ml",
                "shop_image": "/jzgyl/public/upload/20180727/54da509e26d38c6ab985d8ae05a04062.png",
                "shop_gprice": 0,
                "brand_name": "FX/参天",
                "cate_name": "家具日用",
                "bc_price": 0
            },
            {
                "id": 19,
                "shop_name": "GEKKA 睡眠面膜 保湿美白收缩毛孔 80g",
                "shop_image": "/jzgyl/public/upload/20180727/41379e4fdf7d34a93b829bd6c16747ff.png",
                "shop_gprice": 0,
                "brand_name": "GEKKA",
                "cate_name": "面膜",
                "bc_price": 0
            },
            {
                "id": 20,
                "shop_name": "HABA 鲨烷精纯油 白色 美容 15ml",
                "shop_image": "/jzgyl/public/upload/20180727/8bc8ebc83b2e1dc6929590f8502aefe9.png",
                "shop_gprice": 0,
                "brand_name": "HABA",
                "cate_name": "护肤",
                "bc_price": 0
            },
            {
                "id": 23,
                "shop_name": "Naturie/娥佩兰 薏仁化妆水 美白保湿防晒 500ml",
                "shop_image": "/jzgyl/public/upload/20180727/5b9035760b188335ec4300a1079890f4.png",
                "shop_gprice": 0,
                "brand_name": "Naturie/娥佩兰",
                "cate_name": "护肤",
                "bc_price": 0
            }
        ],
        "totalpage": 4
    }
}
```


### 根据类目获取所有上架商品
```
GET /shop/findByCateId
```

参数
```
id -> 类目id
page
size
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "totalpage": 4,
        "shopList": [
            {
                "id": 15,
                "shop_name": "DHC/蝶翠诗 护唇膏 橄榄保湿 1.5g",
                "shop_image": "/jzgyl/public/upload/20180727/38fd11ae1970944bc8ccdfbb87722a6c.jpg",
                "shop_gprice": 0,
                "bc_price": 0
            },
            {
                "id": 94,
                "shop_name": "Canmake/井田 24h晚安粉 素肌保湿控油 4.5g",
                "shop_image": "/jzgyl/public/upload/20180727/0b0459d957c1c4cf8a388a32f1b0381c.png",
                "shop_gprice": 0,
                "bc_price": 0
            },
            {
                "id": 95,
                "shop_name": "Canmake/井田 粉饼棉花糖粉饼  MB哑光自然肌 细腻定妆不飞粉 10g",
                "shop_image": "/jzgyl/public/upload/20180727/1d99eb21daaa8fb93cf430e26a7db6fd.png",
                "shop_gprice": 0,
                "bc_price": 0
            }
        ]
    }
}
```

### 商品详情
```
GET /shop/detail
```
参数
```
id
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": {
        "id": 20,
        "wenan": "",
        "shop_name": "HABA 鲨烷精纯油 白色 美容 15ml",
        "shop_image": "/jzgyl/public/upload/20180727/8bc8ebc83b2e1dc6929590f8502aefe9.png",
        "shop_count": 2,
        "shop_gprice": 0,
        "brand_name": "HABA",
        "shop_jweight": 0,
        "shop_dweight": 0,
        "cate_name": "护肤",
        "bc_price": 0,
        "shop_lprice": 208,
        "bc_cess": 26.37,
        "bc_cprice": 0
    }
}
```

### 商品上架

```
GET /shop/up
```

参数
```
productId
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```

### 商品下架

```
GET /shop/down
```

参数
```
productId
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```