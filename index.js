/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { Component } from 'react'
import { StyleSheet, Text, View, AppRegistry, Button, NativeModules } from 'react-native'

const HelloWorld = NativeModules.HelloWorld
export default class App extends Component {
  componentDidMount () {
    alert(JSON.stringify(NativeModules))
  }
  async sayHiFromJava () {
    HelloWorld.sayHi((err) => { console.log(err) }, (msg) => { console.log(msg) })
  }
  render () {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>Welcome to React Native!</Text>
        <Button title='Press me' onPress={this.sayHiFromJava} />

      </View>
    )
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF'
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5
  }
})

AppRegistry.registerComponent('VFRO-Android-Development', () => App)
