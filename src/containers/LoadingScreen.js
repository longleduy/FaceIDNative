import React, { memo } from 'react';
import LoadingForm from '../components/LoadingForm';

const LoadingScreen = memo(props => {
    let { navigation } = props;
    return <LoadingForm navigation={navigation} />
})
export default LoadingScreen;