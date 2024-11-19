import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_adm_platform_interface.dart';

/// An implementation of [FlutterAdmPlatform] that uses method channels.
class MethodChannelFlutterAdm extends FlutterAdmPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_adm');

  @override
  Future<String?> getRegistrationId() async {
    final id = await methodChannel.invokeMethod<String>('getRegistrationId');
    return id;
  }

  @override
  Future suscribeToTopic(String topic) async {
    await methodChannel.invokeMethod('suscribeToTopic', <String, dynamic>{
    'topic': topic,
    });
  }
}
