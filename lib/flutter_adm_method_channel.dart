import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_adm_platform_interface.dart';

/// An implementation of [FlutterAdmPlatform] that uses method channels.
class MethodChannelFlutterAdm extends FlutterAdmPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final _channel = const MethodChannel('flutter_adm');

  @override
  Future<void> initialize() {
    return _channel.invokeMethod('initialize');
  }

  @override
  void setOnRegistrationId(Function(String) callback) {
    _channel.setMethodCallHandler((call) async {
      print('>>> setOnRegistrationId');
      if (call.method == 'onRegistrationId') {
        callback(call.arguments as String);
      }
    });
  }

  @override
  void setOnMessage(Function(String) callback) {
    _channel.setMethodCallHandler((call) async {
      if (call.method == 'onMessage') {
        callback(call.arguments as String);
      }
    });
  }
}
