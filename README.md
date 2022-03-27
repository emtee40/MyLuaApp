# MyLuaApp
![[GitHub license](https://img.shields.io/github/license/dingyi222666/MyLuaApp)](https://github.com/dingyi222666/MyLuaApp/blob/main/LICENSE)
![[Telegram](https://img.shields.io/badge/Join-Telegram-blue)](https://t.me/MyLuaApp)
![[QQ](https://img.shields.io/badge/Join-QQ_Group-ff69b4)](https://jq.qq.com/?_wv=1027&k=XnJ4FMvS)   
![Minimum SDK](https://img.shields.io/badge/Minimum%20SDK-26-%23ff5252)
![CI Build Stauts](https://github.com/dingyi222666/MyLuaApp/actions/workflows/android_workflow.yml/badge.svg)

nglish | [中文](https://github.com/dingyi222666/MyLuaApp/tree/master/README_ZH.md)

It's ***Work In Progress***.The project is still in the **alpha** stage, and most of the Features are **not implemented** (such as build module). At present, this project can not be fully run.

MyLuaApp is a light and faster software run in **Android Arm Devices** to develop Android software with **Lua or Java**.

You can use lua to develop Android software! The project use [AndroLua_pro](https://github.com/nirenr/AndroLua_pro) runtime environment of the software, run much faster than the original [AndroLua](https://github.com/mkottman/AndroLua)

If you've never used Lua before, that's fine. You can still use Java to develop software, which support java or mixed of Lua and Java to develop, but there is no support plan for ndk for the time being.

This project used Lua to parse the project configuration script. Based on the flexibility of lua, it may even be possible to dynamically add packaging tasks to the build system in the future.

## Features
- [x] JavaC supports (ecj)
- [x] AAPT2 supports
- [x] D8 and R8 supports
- [x] Maven Dependency resolution supports
- [x] File Tree List
- [ ] Multi-module supports(*)
- [ ] TextMate supports (tm4e)(*)
- [ ] Project template(*)
- [ ] Plugin Module(*)
- [ ] Build Module(*)
- [ ] Auto completion supports for Lua
- [ ] Auto completion supports for Java
- [ ] Auto completion supports for Xml

Features marked with an asterisk (*) represent feature currently development

## Build
1. Clone this repository to local.
2. Open it on Android Studio.
3. Use gradle to build it.

## Contribution
We welcome everyone to Contribution this project, you can create an issue or submit pull requests, we welcome contributions to anyone!

## Test Version
Use github actions, the alpha version of the project can be automatically packaged every time it is committed. Click [here](https://github.com/dingyi222666/MyLuaApp/actions).
To get the alpha version of the project generated by the last committed.

## Discuss
 - QQ Group:[1020019846](https://jq.qq.com/?_wv=1027&k=zGdBLMr8)
 - [Telegram Group](https://t.me/MyLuaApp)
