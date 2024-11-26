
import 'flutter_adm_platform_interface.dart';

class FlutterAdm {
  void initialize() {
    FlutterAdmPlatform.instance.initialize();
  }

  void startUnregister() {
    FlutterAdmPlatform.instance.startUnregister();
  }

  void startRegister() {
    FlutterAdmPlatform.instance.startRegister();
  }

  void setOnRegistrationId(Function(String) callback) {
    FlutterAdmPlatform.instance.setOnRegistrationId(callback);
  }

  void setOnSubscription(Function(String, bool) callback) {
    FlutterAdmPlatform.instance.setOnSubscription(callback);
  }

  Future<void> setTopicSubscription(String topic, bool suscribe) async {
    return FlutterAdmPlatform.instance.setTopicSubscription(topic, suscribe);
  }

  void setOnMessage(Function(String) callback) {
    FlutterAdmPlatform.instance.setOnMessage(callback);
  }

  Future<bool> isSupported() async {
    return FlutterAdmPlatform.instance.isSupported();
  }
}
