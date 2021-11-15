
import './App.scss';
import Default from './components/Default/Default';
import InstanceDetails from './components/InstanceDetails/InstanceDetails';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Redirect,
  HashRouter
} from 'react-router-dom';

function App() {
  return (
    <div className="App">
      <Router>
        <HashRouter>
          <Switch>
            <Route exact path="/" component={Default} />
            <Route exact path="/instance_details" component={InstanceDetails} />
            <Redirect from="*" to="/" />
          </Switch>
        </HashRouter>
      </Router>
    </div>
  );
}

export default App;
