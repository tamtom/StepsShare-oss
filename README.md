# 🚶‍♂️ StepsShare

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-blue.svg)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.8.2-orange.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

A modern, cross-platform fitness tracking application built with **Kotlin Multiplatform** and **Compose Multiplatform**. Track your daily steps, set personalized goals, and monitor your fitness progress across Android and iOS devices.

## 📱 Download

Get StepsShare on your device today!

<div align="center">
  <table>
    <tr>
      <td>
        <a href="https://play.google.com/store/apps/details?id=com.itdeveapps.stepsshare">
          <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="60">
        </a>
      </td>
      <td style="width: 20px;"></td>
      <td>
        <a href="https://apps.apple.com/us/app/steps-share-pedometer/id6751459595">
          <img src="https://tools.applemediaservices.com/api/badges/download-on-the-app-store/black/en-us?size=250x83" alt="Download on the App Store" height="60">
        </a>
      </td>
    </tr>
  </table>
</div>

## ✨ Features

### 🎯 **Core Functionality**
- **Step Tracking**: Real-time step counting with device sensors
- **Goal Setting**: Customizable daily goals for steps, calories, distance, and time
- **Progress Monitoring**: Visual progress indicators and achievement tracking
- **Streak Tracking**: Maintain motivation with daily streak counting

### 📊 **Analytics & Insights**
- **Trending Charts**: Beautiful data visualization for your fitness journey
- **Activity Metrics**: Comprehensive tracking of calories burned, distance covered, and active time
- **Historical Data**: View your progress over time with weekly and monthly views
- **Personalized Calculations**: Accurate metrics based on your profile (age, weight, height, gender)

### 🎨 **User Experience**
- **Modern UI**: Material Design 3 with beautiful gradients and animations
- **Onboarding Flow**: Personalized setup experience for new users
- **Cross-Platform**: Seamless experience on both Android and iOS
- **Offline First**: All data stored locally on your device for privacy

### 🔒 **Privacy & Security**
- **Local Storage**: Your data never leaves your device
- **No Cloud Sync**: Complete privacy and data ownership
- **Permission Control**: Transparent permission requests for step counting

## 🏗️ Architecture

StepsShare follows clean architecture principles with a modern tech stack:

```
┌─────────────────────────────────────────────────────────────┐
│                        Presentation Layer                   │
├─────────────────────────────────────────────────────────────┤
│  • Compose UI Components                                   │
│  • ViewModels (MVVM Pattern)                              │
│  • Navigation (Compose Navigation)                        │
├─────────────────────────────────────────────────────────────┤
│                       Domain Layer                         │
├─────────────────────────────────────────────────────────────┤
│  • Use Cases                                              │
│  • Domain Models                                          │
│  • Repository Interfaces                                  │
├─────────────────────────────────────────────────────────────┤
│                       Data Layer                           │
├─────────────────────────────────────────────────────────────┤
│  • Repository Implementations                             │
│  • Local Data Sources (Room Database)                     │
│  • Platform-Specific Implementations                      │
├─────────────────────────────────────────────────────────────┤
│                      Platform Layer                        │
├─────────────────────────────────────────────────────────────┤
│  • Android Implementation                                  │
│  • iOS Implementation                                      │
│  • Shared Business Logic                                   │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Tech Stack

### **Core Technologies**
- **Kotlin Multiplatform**: Cross-platform development
- **Compose Multiplatform**: Modern declarative UI framework
- **Material Design 3**: Beautiful, accessible design system

### **Dependencies**
- **Dependency Injection**: Koin for clean architecture
- **Database**: Room for local data persistence
- **Navigation**: Compose Navigation for screen management
- **Charts**: Compose Charts for data visualization
- **Settings**: Multiplatform Settings for user preferences
- **Serialization**: Kotlinx Serialization for data handling

### **Platform Support**
- **Android**: API 26+ (Android 8.0+)
- **iOS**: iOS 13.0+
- **Architectures**: ARM64, x86_64 (iOS Simulator)

## 🚀 Getting Started

### Prerequisites
- **Android Studio Hedgehog** or later
- **Xcode 15.0** or later (for iOS development)
- **Kotlin 1.9.0** or later
- **JDK 11** or later

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/StepsShare-oss.git
   cd StepsShare-oss
   ```

2. **Open in Android Studio**
   - Open the project in Android Studio
   - Sync Gradle files
   - Build the project

3. **Run on Android**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio

4. **Run on iOS**
   - Open `iosApp/iosApp.xcodeproj` in Xcode
   - Select your target device or simulator
   - Click "Run" in Xcode




## 🤝 Contributing

We welcome contributions from the community! Here's how you can help:

### **Ways to Contribute**
- 🐛 **Report Bugs**: Open an issue with detailed bug reports
- 💡 **Feature Requests**: Suggest new features or improvements
- 📝 **Documentation**: Help improve our docs and code comments
- 🔧 **Code Contributions**: Submit pull requests for bug fixes or features

### **Development Setup**
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes and commit: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### **Code Style**
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Write unit tests for new features

## 🌟 Star History

[![Star History Chart](https://api.star-history.com/svg?repos=yourusername/StepsShare-oss&type=Date)](https://star-history.com/#yourusername/StepsShare-oss&Date)

---

<div align="center">
  <p>If you find this project helpful, please give it a ⭐️!</p>
</div>