
import 'flutter_adm_platform_interface.dart';

class FlutterAdm {
  Future<String?> getPlatformVersion() {
    return FlutterAdmPlatform.instance.getPlatformVersion();
  }
}
