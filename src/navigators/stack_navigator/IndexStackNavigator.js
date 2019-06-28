import { Easing, Animated } from 'react-native';
import { createStackNavigator } from 'react-navigation';
import IndexScreen from '../../containers/IndexScreen';
import FaceScanerScreen from '../../containers/FaceScanerScreen'

export const IndexStackNavigator = createStackNavigator({
    IndexStack: IndexScreen,
    FaceScanerStack: FaceScanerScreen
}, {
        transitionConfig: () => ({
            transitionSpec: {
                duration: 300,
                easing: Easing.out(Easing.poly(4)),
                timing: Animated.timing,
                useNativeDriver: true,
            },
            screenInterpolator: sceneProps => {
                const { layout, position, scene } = sceneProps;
                const { index } = scene;
                const width = layout.initWidth;
                const translateX = position.interpolate({
                    inputRange: [index - 1, index],
                    outputRange: [width, 0],
                })
                return { transform: [{ translateX }] };
            },
        })
    })

