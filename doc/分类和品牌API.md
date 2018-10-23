### 查询所有分类信息

```
GET /cate/list
```

参数
```
无参
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": [
        {
            "id": 22,
            "name": "洁面"
        },
        {
            "id": 23,
            "name": "化妆水"
        },
        {
            "id": 20,
            "name": "彩妆"
        },
        {
            "id": 21,
            "name": "面膜"
        },
        {
            "id": 24,
            "name": "护肤"
        },
        {
            "id": 25,
            "name": "美容保健"
        },
        {
            "id": 26,
            "name": "减肥瘦身"
        },
        {
            "id": 27,
            "name": "洗护"
        },
        {
            "id": 28,
            "name": "家具日用"
        },
        {
            "id": 30,
            "name": "食品"
        }
    ]
}
```

### 新增类目
```
POST /cate/save
```

参数
```
name
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```

### 更新类目
```
POST /cate/update
```

参数
```
id
name
```

返回
```
{
    "code": 500153,
    "msg": "名称为: '男生最爱'的分类已经存在"
}
```

### 删除类目（批量删除和单个删除二合一，批量删除，id 使用 ‘-’ 进行拼接传递）

```
GET /cate/delete
```

参数
```
cateIds -> 一或多id
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```

## 品牌 API

### 按照名称模糊查询

```
GET /brand/findByName
```

参数
```
name: D
```

返回
```
{
    "code": 0,
    "msg": "成功",
    "data": [
        {
            "id": 11,
            "name": "Deonatulle",
            "createTime": 1532597229000,
            "updateTime": 1532597229000
        },
        {
            "id": 12,
            "name": "DHC/蝶翠诗",
            "createTime": 1532597237000,
            "updateTime": 1532597237000
        },
        {
            "id": 27,
            "name": "Lucky Trendy",
            "createTime": 1532597364000,
            "updateTime": 1532597364000
        },
        {
            "id": 31,
            "name": "MSD",
            "createTime": 1532597440000,
            "updateTime": 1532597440000
        },
        {
            "id": 39,
            "name": "PDC/碧迪皙",
            "createTime": 1532597766000,
            "updateTime": 1532597766000
        },
        {
            "id": 61,
            "name": "Dr.Ci.Labo/城野医生",
            "createTime": 1532598065000,
            "updateTime": 1532598065000
        },
        {
            "id": 63,
            "name": "DAISO/大创",
            "createTime": 1532598087000,
            "updateTime": 1532598087000
        },
        {
            "id": 81,
            "name": "LADUREE/拉杜丽",
            "createTime": 1532598312000,
            "updateTime": 1532598312000
        },
        {
            "id": 86,
            "name": "Mandom/曼丹",
            "createTime": 1532598350000,
            "updateTime": 1532598350000
        },
        {
            "id": 90,
            "name": "Nadnatura/奈娅蒂",
            "createTime": 1532598408000,
            "updateTime": 1532598408000
        },
        {
            "id": 108,
            "name": "Shiseido/ 资生堂",
            "createTime": 1532598559000,
            "updateTime": 1532598559000
        },
        {
            "id": 113,
            "name": "Decorte/黛珂",
            "createTime": 1532692645000,
            "updateTime": 1532692645000
        }
    ]
}
```

### 获取品牌列表（分页）

```
GET /brand/list
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
        "data": [
            {
                "id": 8,
                "name": "Club",
                "createTime": 1532596980000,
                "updateTime": 1532596980000
            },
            {
                "id": 7,
                "name": "Canmake/井田",
                "createTime": 1532596964000,
                "updateTime": 1532596964000
            },
            {
                "id": 9,
                "name": "Cow/牛乳",
                "createTime": 1532597129000,
                "updateTime": 1532597129000
            },
            {
                "id": 10,
                "name": "Covermark/傲丽",
                "createTime": 1532597186000,
                "updateTime": 1532597186000
            },
            {
                "id": 11,
                "name": "Deonatulle",
                "createTime": 1532597229000,
                "updateTime": 1532597229000
            },
            {
                "id": 12,
                "name": "DHC/蝶翠诗",
                "createTime": 1532597237000,
                "updateTime": 1532597237000
            },
            {
                "id": 13,
                "name": "Elegance/雅莉格丝",
                "createTime": 1532597244000,
                "updateTime": 1532597244000
            },
            {
                "id": 15,
                "name": "Fancl/芳珂",
                "createTime": 1532597261000,
                "updateTime": 1532597261000
            },
            {
                "id": 16,
                "name": "Fujiko",
                "createTime": 1532597267000,
                "updateTime": 1532597267000
            },
            {
                "id": 17,
                "name": "FX/参天",
                "createTime": 1532597276000,
                "updateTime": 1532597276000
            }
        ],
        "totalPage": 11
    }
}
```


### 新增品牌

```
POST /brand/save
```

参数
```
name
level
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```

### 更新品牌

```
POST /brand/update
```

参数
```
id
name
level
```

返回
```
{
    "code": 0,
    "msg": "成功"
}
```


### 删除品牌

```
GET /brand/delete
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

