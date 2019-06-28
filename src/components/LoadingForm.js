import React, { memo, useEffect } from 'react';
import { View } from 'react-native';
import { Transition } from 'react-navigation-fluid-transitions';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
//Todo: Styles
import AppStyle from '../theme/index';

const LoadingForm = memo(props => {
    let { navigation } = props;
    useEffect(() => {
        setTimeout(() => {
            navigation.navigate('IndexStack')
        }, 500)
    }, [])
    return (
        <View style={AppStyle.StyleMain.flexViewCenter}>
            <Transition shared='logo'>
                <Icon
                    name='shield-account'
                    size={150}
                    color={AppStyle.styleVariable.mainColor} />
            </Transition>
        </View>
    )
})
export default LoadingForm;