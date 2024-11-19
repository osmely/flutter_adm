
import 'flutter_adm_platform_interface.dart';

class FlutterAdm {
  Future<String?> getRegistrationId() {
    return FlutterAdmPlatform.instance.getRegistrationId();
  }

  Future suscribeToTopic(String topic) {
    return FlutterAdmPlatform.instance.suscribeToTopic(topic);
  }
}
