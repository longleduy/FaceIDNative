import {
    StyleSheet,Dimensions
} from 'react-native';
import styleVariable from './StyleVariable';

const StyleMain = StyleSheet.create({
    flexViewCenter: { flex: 1, justifyContent: 'center', alignItems: 'center',flexDirection: 'column', },
    flexView: { flex: 1,alignItems: 'center'},
    flexViewNotFlex1: {justifyContent: 'center', alignItems: 'center' },
    width100:styleVariable.width100,
    height100:styleVariable.height100,
    textInputError: {color:styleVariable.mainColor,borderColor:styleVariable.mainColor}
});
export default StyleMain;