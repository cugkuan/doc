# 概述

先自己玩一下大模型。

目标：
> 本地运行一个模型，然后学习《论语》这本书，RAG 模式。


## 本地模型部署

- 安装Ollama 
> brew install ollama

安装完成后，有二种模式：

后台常驻： brew services start ollama

手动方式（仅在一个终端运行）：OLLAMA_FLASH_ATTENTION="1" OLLAMA_KV_CACHE_TYPE="q8_0" ollama serve

- 跑模型
跑了一个千问的模型
> ollama run qwen3.5:1.5b


好了，本地模型跑起来了。

## RAG 工具

- pip3 install langchain  Ai开发框架
- pip3 install chromadb  向量数据库
- pip3 install sentence-transformers  文本向量化
- pip3 install ollama  调用本地模型