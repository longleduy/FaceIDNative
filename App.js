/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, { Component } from 'react';
import { View } from 'react-native';
import MainNavigator from './src/navigators/MainNavigator';
export default class App extends Component {
  render() {
    return (
      <View style={{ flex: 1 }}>
        <MainNavigator />
      </View>
    );
  }
}

