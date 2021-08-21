package com.dingyi.lua.analyze.info;

/**
 * @author: dingyi
 * @date: 2021/8/20 23:20
 * @description:
 **/
public class PackageInfo extends FunctionCallInfo  {
    @Override
    public String toString() {
        return "PackageInfo{" +
                "range=" + range +
                ", isLocal=" + isLocal +
                ", isArg=" + isArg +
                ", name='" + name + '\'' +
                ", parent=" + parent +
                ", type=" + type +
                ", value=" + value +
                ", code='" + code + '\'' +
                '}';
    }
}
