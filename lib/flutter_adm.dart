
import 'flutter_adm_platform_interface.dart';

class FlutterAdm {
  Future<void> initialize() {
    return FlutterAdmPlatform.instance.initialize();
  }

  void setOnRegistrationId(Function(String) callback) {
    return FlutterAdmPlatform.instance.setOnRegistrationId(callback);
  }

  void setOnMessage(Function(String) callback) {
    FlutterAdmPlatform.instance.setOnMessage(callback);
  }
}
