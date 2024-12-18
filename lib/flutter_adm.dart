
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

  Future<void> setTopicSubscription(String topic, bool suscribe) async {
    return FlutterAdmPlatform.instance.setTopicSubscription(topic, suscribe);
  }

  void setOnMessage(Function(String) callback) {
    FlutterAdmPlatform.instance.setOnMessage(callback);
  }

  void setOnNotificationClicked(Function(String) callback) {
    FlutterAdmPlatform.instance.setOnNotificationClicked(callback);
  }

  Future<bool> isSupported() async {
    return FlutterAdmPlatform.instance.isSupported();
  }

  Future<String?> getInitialMessage() async {
    return FlutterAdmPlatform.instance.getInitialMessage();
  }

  Future<String?> getRegistrationId() async {
    return FlutterAdmPlatform.instance.getRegistrationId();
  }
}
