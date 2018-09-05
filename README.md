# ClinMDEquilSmartPen

   signingConfigs {
        config {
            keyAlias '28101995'
            keyPassword '28101995'
            storeFile file('C:/Users/mbbbj/Desktop/ClinMDEquilSmartPen/app/clinmd.jks')
            storePassword '28101995'
        }
    }








  release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.txt'
            signingConfig signingConfigs.config
        }
