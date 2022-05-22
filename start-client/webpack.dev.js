/* eslint-disable */

const { merge } = require('webpack-merge')
const common = require('./webpack.common.js')
const path = require('path')

const mock = require('./dev/api.mock.json')
const mock_oauthapps = require('./dev/api.mock_oauthapps.json')
const fs = require('fs')

const config = {
  mode: 'development',
  devtool: 'inline-source-map',
  devServer: {
    static: {
      directory: path.resolve(__dirname, 'public')
    },
    
    historyApiFallback: true,
    compress: true,
    open: false,
    onBeforeSetupMiddleware: function(devServer) {
      devServer.app.get('/metadata/client', function(req, res) {
        setTimeout(() => {
          res.json(mock)
        }, 800)
      })
      devServer.app.get('/metadata/oauthapps', function(req, res) {
        setTimeout(() => {
          res.json(mock_oauthapps)
        }, 800)
      })
      devServer.app.get('/starter.zip', function(req, res) {
        fs.readFile(path.resolve('./dev/starter.mock.zip'), (err, data) => {
          if (err) return sendError(err, res)
          setTimeout(() => {
            res.send(data)
          }, 800)
        })
      })
    },
  },
}

module.exports = merge(common, config)
