import {  createAppContainer } from 'react-navigation';
import { createFluidNavigator } from 'react-navigation-fluid-transitions';
import LoadingScreen from '../containers/LoadingScreen';
import { IndexStackNavigator } from './stack_navigator/IndexStackNavigator';

const MainNavigator = createFluidNavigator({
    IndexStack: IndexStackNavigator,
    Loading: {
        screen: LoadingScreen
    },
    
    
})
export default createAppContainer(MainNavigator);