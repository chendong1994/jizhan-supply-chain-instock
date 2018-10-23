### 新增商品
```
POST /product/provider/repoAdd
```

参数
```
providerId：
productId:
purchasePrice:
productStock
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
POST /product/provider/repoUpdate
```

参数
```
id:
providerId：
productId:
purchasePrice:
productStock
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
GET /product/provider/repoDel
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




### 根据商品 id 查各个供应商报价
```
GET /product/provider/findProviderByProductId
```

参数
```
productId:
```

返回
```
{
    "code": 0,
    "msg": "成功",
    data: [
        {
            providerId:
            productName:
            productJancode:
            boxQuantity:
            purchasePrice:
            productStock:
            updateTime:
        },
        {
            providerId:
            productName:
            productJancode:
            boxQuantity:
            purchasePrice:
            productStock:
            updateTime:
        }
    ]
}
```




### 根据商品 id 查供应商的商品库
```
GET /product/provider/findProviderByProductId
```

参数
```
productId:
providerId:
```

返回
```
{
    "code": 0,
    "msg": "成功",
    data: 
    {
          id:
          providerId:
          productId:
          productName:
          productJancode:
          boxQuantity:
          purchasePrice:
          productStock:
          createTime:
          updateTime:
    }
}
```