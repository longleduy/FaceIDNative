import React, { memo, useEffect, useState } from 'react';
import { View, Text, Image, Alert } from 'react-native';
import { Kohana } from 'react-native-textinput-effects';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
import FingerPrintAuth from '../native_modules/FingerPrintAuthModule'
//Todo: Styles
import AppStyle from '../theme/index';

const FaceScanerForm = memo(props => {
    let { navigation } = props;
    const [faceData, setFaceData] = useState({
        image: null,
        loading: false
    });
    getIsSupported = () => {
        FingerPrintAuth.getFingerPrintAuth(
            (msg) => {
                Alert.alert(JSON.stringify(msg))
            },
            (result) => {
                let image = `data:image/jpg;base64,${result}`;
                setFaceData({
                    image,
                    loading: false
                })
            },
        );
    }
    useEffect(() => {
        getIsSupported();
      },[]);
    return (
        <View style={{ flex: 1 }}>
            <View style={{ flex: 1, flexGrow: 1, justifyContent: 'center', alignItems: 'center' }}>
                    <Image width={AppStyle.styleVariable.width100} height={AppStyle.styleVariable.height100 *.8} source={{ uri: faceData.image }} />

            </View>
            <View style={{ flexDirection: 'row', marginVertical: 10 }}>
                <Kohana
                    label='Nhập ID'
                    iconClass={Icon}
                    iconName='account-key'
                    inputStyle={{ color: '#666', fontSize: 15 }}
                    inputPadding={14}
                    style={{
                        borderColor: '#ccc',
                        borderRadius: 5,
                        justifyContent: 'center',
                        alignItems: 'center'
                    }}
                    useNativeDriver
                />
            </View>
        </View>
    )
})
export default FaceScanerForm;