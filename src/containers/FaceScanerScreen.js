import React, { PureComponent } from 'react';
import FaceScanerForm from '../components/FaceScanerForm';

export default class FaceScanerScreen extends PureComponent {
    static navigationOptions = ({ navigation }) => {
        return {
            title: "Quét khuôn mặt"
        }
    }
    render() {
        return (
            <FaceScanerForm navigation={this.props.navigation} />
        )
    }
}