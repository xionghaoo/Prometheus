## 待完成
+ 二维码生成
[android-using-zxing-generate-qr-code](https://stackoverflow.com/questions/28232116/android-using-zxing-generate-qr-code/30529128)

## 屏幕适配

#### 系统如何计算屏幕信息？
> 相关方法 DisplayInfo.getMetricsWithSize()
```java
private void getMetricsWithSize(DisplayMetrics outMetrics, CompatibilityInfo compatInfo,
        Configuration configuration, int width, int height) {
    outMetrics.densityDpi = outMetrics.noncompatDensityDpi = logicalDensityDpi;
    outMetrics.density = outMetrics.noncompatDensity =
            logicalDensityDpi * DisplayMetrics.DENSITY_DEFAULT_SCALE;
    outMetrics.scaledDensity = outMetrics.noncompatScaledDensity = outMetrics.density;
    outMetrics.xdpi = outMetrics.noncompatXdpi = physicalXDpi;
    outMetrics.ydpi = outMetrics.noncompatYdpi = physicalYDpi;

    final Rect appBounds = configuration != null
            ? configuration.windowConfiguration.getAppBounds() : null;
    width = appBounds != null ? appBounds.width() : width;
    height = appBounds != null ? appBounds.height() : height;

    outMetrics.noncompatWidthPixels  = outMetrics.widthPixels = width;
    outMetrics.noncompatHeightPixels = outMetrics.heightPixels = height;

    if (!compatInfo.equals(CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO)) {
        compatInfo.applyToDisplayMetrics(outMetrics);
    }
}
```
#### 系统固有参数
+ logicalDensityDpi 系统固定的dpi
+ DisplayMetrics.DENSITY_DEFAULT_SCALE = 1 / 160，160为mdpi基准密度

#### dpi计算方式
> 屏幕实际的dpi
```java
physicalXDpi = sqrt(physicalWidth ^ 2 + physicalHeight ^ 2) / diagonalSize
```
其中
+ physicalXDpi 手机的物理dpi值，这个值跟手机的尺寸有关
+ physicalWidth、physicalHeight 屏幕宽高，单位为pixel
+ diagonalSize 对角线的尺寸，单位英尺

#### density计算
> 屏幕缩放因子，屏幕适配的关键参数
```java
outMetrics.density = outMetrics.noncompatDensity =
            logicalDensityDpi * DisplayMetrics.DENSITY_DEFAULT_SCALE;
```

#### 如何适配？
> 以Nexus6p(1440 x 2560, 3.5, 560dpi, 515dpi)和华为nova3e(1080 x 2280, 3, 480dpi, )为例

[Google官方的dpi说明](https://developer.android.com/training/multiscreen/screendensities?hl=zh-CN)

| density qualifier | dpi     | density | screen width(dp) | screen width(pixel) |
| ----------------- | ------- | ------- | ---------------- | ------------------- |
| ldpi              | ~120dpi | 0.75x   |                  |                     |
| mdpi              | ~160dpi | 1x      |                  |                     |
| hdpi              | ~240dpi | 1.5x    |                  |                     |
| xhdpi             | ~320dpi | 2x      | 360dp            | 720                 |
| xxhdpi            | ~480dpi | 3x      | 360dp            | 1080                |
| xxxhdpi           | ~640dpi | 4x      | 360dp            | 1440                |

参照上面的说明，Nexus6p并不是标准的尺寸，而华为nova3e是标准尺寸。设计稿是以1080 3x为标准，如果屏幕是符合上表的标准屏幕，是完全适配的。
像Nexus6p这种非标准屏幕如何适配呢？Google提供了按最小宽度来适配的方法，这里的最小宽度是以dp为单位。

先来看看Nexus6p的参数：

1. 屏幕 1440 x 2560， 对应的宽度 1440 / 3.5 = 411dp

2. 缩放因子 density = 3.5

3. 逻辑dpi = 560dpi，属于 480dpi ～ 640dpi的范围，资源文件为xxxhdpi

4. 实际dpi = 515dpi，未用到

如果标准屏幕的100dp放在Nexus6p上应该为114dp
```
100dp * 411dp / 360dp = 114dp
```

##### 该放在那个资源文件夹下面？

```
values-sw410dp-xxxhdpi/dimens.xml
```

当然你写成values-sw410dp或values-sw410dp-xhdpi都是可以的，但是Android系统会优先寻找xxxhdpi文件夹下的尺寸文件。

其实如果存在values-sw410dp-xhdpi和values-sw410dp-xxxhdpi，那么这两个文件夹下的尺寸是一样的，因为是按屏幕宽度(dp)计算的百分比尺寸。

因此最终我们关心的尺寸是屏幕的dp尺寸。

#### 现有测试机型

| Phone     | density | width(dp) |
| --------- | ------- | --------- |
| Nexus6p   | 3.5     | 411dp     |
| Mi8       | 2.75    | 392dp     |
| Oppo A57  | 2       | 360dp     |
| Nova 3e   | 3       | 360dp     |

Google提供的机型信息 [非国产手机尺寸信息](https://material.io/tools/devices/)

目前没有国产手机比较官方的destiny统计信息，先阶段只能自己收集。

由上述机型，我们可以生成如下文件夹：
```
values/dimens.xml
values-sw390dp/dimens.xml
values-sw410dp/dimens.xml
```
### 1080*1920屏幕UI稿对应适配宽高
> 以宽度为标准适配时要注意高度的适配

| width(dp) | density | width(pixel) | adapt height(pixel)    | origin height(pixel) |
| --------- | ------- | ---------    | -------------          | ---                  |
| 360dp     | 3       | 1080         | 1920                   | 1920                 |
| 411dp     | 3.5     | 1440         | 640×411/360*3.5 = 2559 | 2560(包含导航栏)      |
| 392dp     | 2.75    | 1080         | 640×392/360×2.7 = 1882 | -                    |