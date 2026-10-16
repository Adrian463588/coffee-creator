# BDD Scenarios: Custom App Launcher Icon & Adaptive Android Spec

Feature: Custom Precision Coffee Lab App Launcher Icon
  As a coffee barista and mobile app user
  I want an authentic, recognizable app icon on my home screen and app drawer
  So that Coffee Creator visually reflects its specialized coffee science identity.

  Background:
    Given the Coffee Creator application package "com.coffeecreator.app"
    And Android device supporting Adaptive Icons (API 26+)

  @icon @manifest
  Scenario: AndroidManifest references custom launcher mipmap assets
    Given the compiled AndroidManifest.xml
    When the package manager queries the application icon
    Then "android:icon" must resolve to "@mipmap/ic_launcher"
    And "android:roundIcon" must resolve to "@mipmap/ic_launcher_round"
    And no generic system drawable "@android:drawable/ic_menu_compass" is referenced

  @adaptive_icon @resolution
  Scenario Outline: Adaptive icon assets exist across all target density buckets
    Given the application resource directory "app/src/main/res"
    When the launcher loads the icon for density "<density>"
    Then the asset file "<path>" must exist
    And the image dimensions must match "<width>x<height>" pixels
    Examples:
      | density   | path                                          | width | height |
      | mdpi      | mipmap-mdpi/ic_launcher.png                   | 48    | 48     |
      | hdpi      | mipmap-hdpi/ic_launcher.png                   | 72    | 72     |
      | xhdpi     | mipmap-xhdpi/ic_launcher.png                  | 96    | 96     |
      | xxhdpi    | mipmap-xxhdpi/ic_launcher.png                 | 144   | 144    |
      | xxxhdpi   | mipmap-xxxhdpi/ic_launcher.png                | 192   | 192    |
      | anydpi-v26| mipmap-anydpi-v26/ic_launcher.xml             | -     | -      |
      | anydpi-v26| mipmap-anydpi-v26/ic_launcher_round.xml       | -     | -      |

  @theme_consistency
  Scenario: Icon visual design adheres to Precision Coffee Lab aesthetics
    Given the launcher icon foreground and background layers
    When rendered on dark or light launcher surfaces
    Then the icon features warm espresso and copper palette tones
    And the foreground graphic maintains a 66dp inner safe zone to prevent edge masking
