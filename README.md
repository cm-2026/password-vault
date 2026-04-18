# 密码保险箱 - Android密码管理应用

一个安全、美观的安卓密码管理应用，支持指纹登录、本地加密存储和分类管理。

## 功能特性

### 🔐 安全认证
- 指纹识别登录（适配小米13屏下指纹）
- PIN码备用认证
- 自动锁定功能
- 加密密钥存储在Android Keystore

### 🗃️ 密码管理
- 添加、编辑、删除密码条目
- 分类管理（社交、金融、工作等）
- 收藏重要密码
- 搜索功能
- 复制账号/密码到剪贴板

### 🎨 用户界面
- 暗黑科技风设计
- 流畅的动画效果
- 材料设计3规范
- 高刷新率支持（120Hz）

### 🔒 数据安全
- 本地加密存储（AES-256-GCM）
- 加密备份和恢复
- 无网络连接要求
- 防暴力破解保护

### ⚙️ 其他功能
- 自动备份
- 数据统计
- 多主题支持
- 隐私保护

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构**: MVVM + Repository
- **数据库**: Room + SQLCipher
- **加密**: Android Keystore + AES-GCM
- **依赖注入**: Hilt
- **导航**: Navigation Compose

## 🚀 快速开始

### 方法一：使用GitHub Actions自动构建（推荐）

本项目已配置GitHub Actions，可以自动构建APK：

1. **访问GitHub仓库**: https://github.com/cm-2026/password-vault
2. **查看Actions**: 点击"Actions"标签页
3. **下载APK**: 构建完成后，在Artifacts中下载 `password-vault-apk`
4. **安装到手机**: 将APK文件传输到小米13并安装

### 方法二：本地构建

#### 环境要求
- Android Studio Flamingo 或更高版本
- JDK 17+
- Android SDK 34+

#### 构建步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/cm-2026/password-vault.git
   cd password-vault
   ```

2. **使用构建脚本（Windows）**
   ```bash
   # 双击运行构建脚本
   build-apk.bat
   ```

3. **或手动构建**
   ```bash
   # 调试版本
   ./gradlew assembleDebug
   
   # 发布版本
   ./gradlew assembleRelease
   ```

4. **安装到设备**
   ```bash
   # 通过ADB安装
   adb install app/build/outputs/apk/debug/app-debug.apk
   
   # 或通过文件管理器安装
   # 将APK复制到手机，点击安装
   ```

### 方法三：一键提交到GitHub（Windows用户）

如果你在Windows上，可以使用我们提供的脚本：

1. **运行提交脚本**
   ```bash
   # 双击运行
   push-to-github.bat
   ```

2. **按照提示操作**
   - 输入提交信息
   - 等待推送到GitHub
   - 访问GitHub Actions页面

3. **下载构建好的APK**
   - 访问: https://github.com/cm-2026/password-vault/actions
   - 点击最新的构建任务
   - 在Artifacts中下载APK

### 小米13特别说明

1. **启用开发者选项**
   - 设置 → 关于手机 → 连续点击MIUI版本
   - 返回设置 → 更多设置 → 开发者选项

2. **启用USB调试**
   - 开发者选项 → USB调试（启用）
   - 开发者选项 → USB安装（启用）

3. **关闭MIUI优化（可选）**
   - 开发者选项 → 启用MIUI优化（关闭）

## 项目结构

```
password-vault/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/passwordvault/
│   │   │   ├── data/              # 数据层
│   │   │   │   ├── database/      # Room数据库
│   │   │   │   ├── model/         # 数据模型
│   │   │   │   └── repository/    # 仓库层
│   │   │   ├── di/                # 依赖注入
│   │   │   ├── security/          # 安全模块
│   │   │   ├── ui/                # UI层
│   │   │   │   ├── auth/          # 认证界面
│   │   │   │   ├── components/    # 可复用组件
│   │   │   │   ├── main/          # 主界面
│   │   │   │   ├── password/      # 密码管理
│   │   │   │   ├── settings/      # 设置界面
│   │   │   │   └── theme/         # 主题
│   │   │   └── util/              # 工具类
│   │   └── res/                   # 资源文件
│   └── build.gradle               # 模块配置
├── build.gradle                   # 项目配置
└── settings.gradle               # 项目设置
```

## 安全说明

### 加密机制
1. **主密钥**: 存储在Android Keystore中，无法导出
2. **数据加密**: 使用AES-256-GCM算法
3. **密码存储**: 所有密码在存储前加密
4. **内存安全**: 敏感数据使用后立即清除

### 隐私保护
1. **本地存储**: 所有数据存储在设备本地
2. **无网络权限**: 应用不需要网络连接
3. **无数据收集**: 不收集任何用户数据
4. **开源透明**: 代码完全开源可审计

## 测试

### 单元测试
```bash
./gradlew test
```

### 仪器测试
```bash
./gradlew connectedAndroidTest
```

### 手动测试清单
- [ ] 指纹登录正常
- [ ] PIN码认证正常
- [ ] 密码添加/编辑/删除
- [ ] 分类功能正常
- [ ] 备份恢复功能
- [ ] 自动锁定功能
- [ ] 暗黑主题显示
- [ ] 小米13兼容性

## 故障排除

### 常见问题

1. **指纹识别失败**
   - 检查设备是否支持指纹
   - 重新录入指纹
   - 重启设备

2. **APK安装失败**
   - 检查Android版本兼容性
   - 启用"未知来源"安装
   - 清除旧版本应用

3. **数据库错误**
   - 清除应用数据
   - 重新安装应用
   - 检查存储权限

4. **小米13特定问题**
   - 启用USB安装权限
   - 关闭MIUI优化
   - 检查后台限制

### 日志查看
```bash
# 查看应用日志
adb logcat -s PasswordVault

# 查看错误日志
adb logcat *:E
```

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📱 GitHub自动构建

### GitHub Actions工作流

本项目配置了自动构建流水线，每次推送到main/master分支时都会自动构建APK：

- **工作流文件**: `.github/workflows/android.yml`
- **构建环境**: Ubuntu + JDK 17 + Gradle 8.5
- **构建产物**: 调试版APK (`app-debug.apk`)
- **构建时间**: 约5-10分钟

### 如何获取构建的APK

1. **访问Actions页面**: https://github.com/cm-2026/password-vault/actions
2. **选择最新的构建任务** (绿色对勾✓表示成功)
3. **点击"Artifacts"下拉菜单**
4. **下载"password-vault-apk"**
5. **解压ZIP文件获取APK**

### 构建状态徽章

将以下Markdown添加到你的README中显示构建状态：

```markdown
![Android CI](https://github.com/cm-2026/password-vault/actions/workflows/android.yml/badge.svg)
```

### 本地开发与GitHub同步

```bash
# 首次设置
git init
git remote add origin https://github.com/cm-2026/password-vault.git

# 提交更改
git add .
git commit -m "描述你的更改"

# 推送到GitHub
git push -u origin main

# 如果推送失败，尝试
git push -u origin master
```

## 联系方式

如有问题或建议，请通过以下方式联系：
- **GitHub Issues**: https://github.com/cm-2026/password-vault/issues
- **提交Pull Request**
- **参与讨论**

## 📋 项目状态

![Android CI](https://github.com/cm-2026/password-vault/actions/workflows/android.yml/badge.svg)

**最新构建**: [下载APK](https://github.com/cm-2026/password-vault/actions)

---

**免责声明**: 本应用为开源项目，作者不对使用本应用造成的任何数据丢失或安全问题负责。请定期备份重要数据。

**开源协议**: MIT License - 查看 [LICENSE](LICENSE) 文件了解详情