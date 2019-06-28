import React, { PureComponent } from 'react';
import IndexForm from '../components/IndexForm';

export default class IndexScreen extends PureComponent {
    static navigationOptions = ({ navigation }) => {
        return {
            headerTransparent: true
        }
    }
    render() {
        return (
            <IndexForm navigation={this.props.navigation} />
        )
    }
}
