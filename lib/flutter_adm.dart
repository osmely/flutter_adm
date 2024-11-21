
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

  void setOnSuscription(Function(String, bool) callback) {
    FlutterAdmPlatform.instance.setOnSuscription(callback);
  }

  void setTopicSuscription(String topic, bool suscribe) async {
    await FlutterAdmPlatform.instance.setTopicSuscription(topic, suscribe);
  }

  void setOnMessage(Function(String) callback) {
    FlutterAdmPlatform.instance.setOnMessage(callback);
  }

  Future<bool> isSupported() async {
    return FlutterAdmPlatform.instance.isSupported();
  }
}
