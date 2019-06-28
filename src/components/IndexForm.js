import React, { memo, useEffect } from 'react';
import { View, Text, Button } from 'react-native';
import { Transition } from 'react-navigation-fluid-transitions';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
import Ripple from 'react-native-material-ripple';
//Todo: Styles
import AppStyle from '../theme/index';

const IndexForm = memo(props => {
    let { navigation } = props;
    return (
        <View style={AppStyle.StyleMain.flexViewCenter}>
            <View style={[AppStyle.StyleMain.flexViewCenter,{width:'100%'}]}>
                <Icon
                    name='fingerprint'
                    size={AppStyle.styleVariable.width100 * .20} />
                <Text style={{ fontSize: 15, fontWeight: 'bold', marginVertical: 5 }}>FingerPrint</Text>
            </View>
            <Ripple style={[AppStyle.StyleMain.flexViewCenter,{width:'100%'}]} onPress={() => navigation.navigate('FaceScanerStack')}>
                <Icon
                    name='face-recognition'
                    size={AppStyle.styleVariable.width100 * .20} />
                <Text style={{ fontSize: 15, fontWeight: 'bold', marginVertical: 5 }}>FaceID</Text>
            </Ripple>
        </View>
    )
})
export default IndexForm;