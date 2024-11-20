
import 'flutter_adm_platform_interface.dart';

class FlutterAdm {
  void initialize() {
    FlutterAdmPlatform.instance.initialize();
  }

  void startRegister() {
    FlutterAdmPlatform.instance.startRegister();
  }

  void setOnRegistrationId(Function(String) callback) {
    FlutterAdmPlatform.instance.setOnRegistrationId(callback);
  }

  void setOnMessage(Function(String) callback) {
    FlutterAdmPlatform.instance.setOnMessage(callback);
  }

  Future<bool> isSupported() async {
    return FlutterAdmPlatform.instance.isSupported();
  }
}
