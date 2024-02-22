# 路由

以文件路径为路由地址。它的约定大于配置。其中
> A special page.js file is used to make route segments publicly accessible.

page.js 是唯一的入口文件。

## Page

A page is UI that is unique to a route

Page 就是入口

- A page is always the leaf of the route subtree.
- .js, .jsx, or .tsx file extensions can be used for Pages.
- A page.js file is required to make a route segment publicly accessible.
- Pages are Server Components by default but can be set to a Client Component.
- Pages can fetch data. View the Data Fetching section for more information.

## Layouts
