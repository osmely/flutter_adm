import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_adm/flutter_adm.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _registrationId = 'Unknown';
  final adm = FlutterAdm();

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    
    try {
      adm.setOnMessage((msg) {
        try {
          Map<String, dynamic> jsonMap = jsonDecode(msg);
          print(jsonMap);
          
        } catch (e) {}
      });

      adm.setOnRegistrationId((registrationId) async {
        print('New registration id: $registrationId');
        _registrationId = registrationId;
      });

      adm.setOnNotificationClicked((msg) {
        try {
          Map<String, dynamic> jsonMap = jsonDecode(msg);
          print(jsonMap);
        } catch (e) {}
      });

      adm.initialize();
    } catch (e) {
      print(e);
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('--> $_registrationId\n'),
        ),
      ),
    );
  }
}
