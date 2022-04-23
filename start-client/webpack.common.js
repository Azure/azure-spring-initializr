/* eslint-disable */

const path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const WebpackPwaManifest = require('webpack-pwa-manifest')
const isDev = process.env.NODE_ENV === 'development'
const CopyPlugin = require('copy-webpack-plugin')

const CODE = `
<script type="text/javascript">
  (function(c,l,a,r,i,t,y){
      c[a]=c[a]||function(){(c[a].q=c[a].q||[]).push(arguments)};
      t=l.createElement(r);t.async=1;t.src="https://www.clarity.ms/tag/"+i;
      y=l.getElementsByTagName(r)[0];y.parentNode.insertBefore(t,y);
  })(window, document, "clarity", "script", "{{ID}}");
</script>
`;

class WebpackCodeAppender {
  constructor(id) {
    this.id = id
  }
  apply(compiler) {
    compiler.hooks.compilation.tap('gtag', compilation => {
      HtmlWebpackPlugin.getHooks(compilation).beforeEmit.tapAsync(
        'gtag',
        (htmlPlugin, callback) => {
          if (this.id) {
            htmlPlugin.html = htmlPlugin.html.replace(
              '</body>',
              CODE.replace(new RegExp('{{ID}}', 'g'), this.id) + '</body>'
            )
          }
          callback(null, htmlPlugin)
        }
      )
    })
  }
}

const config = {
  entry: path.resolve(__dirname, 'src/App.js'),
  output: {
    path: path.resolve(__dirname, 'public'),
    publicPath: '/',
    filename: 'main.[id].[fullhash].js',
    chunkFilename: '[id].[chunkhash].js',
  },
  module: {
    rules: [
      {
        test: /.(js|jsx)$/,
        exclude: [path.resolve(__dirname, 'node_modules')],
        loader: 'babel-loader',
      },
      {
        test: /\.s[ac]ss$/i,
        use: ['style-loader', 'css-loader', 'sass-loader'],
      },
      {
        test: /\.(woff(2)?|ttf|eot|svg)(\?v=\d+\.\d+\.\d+)?$/,
        use: [
          {
            loader: 'file-loader',
            options: {
              name: '[hash].[ext]',
              outputPath: 'fonts/',
            },
          },
        ],
      },
    ],
  },
  plugins: [
    new CopyPlugin({
      patterns: [
        {
          from: path.resolve(__dirname, 'static/images'),
          to: path.resolve(__dirname, 'public/images'),
        },
      ],
    }),
    new HtmlWebpackPlugin({
      minify: isDev
        ? false
        : {
          collapseWhitespace: true,
          removeComments: true,
          useShortDoctype: true,
          minifyCSS: true,
        },
      template: './static/index.html',
      title: 'Azure Spring Initializr',
      description: `Initializr generates spring boot project with just what you need to start quickly!`,
      url: 'https://start.spring.io',
      twitter: '@springboot',
      image: `https://start.spring.io/images/initializr-card.jpg`,
      theme: `#6db33f`,
      pageConfig: JSON.stringify({
        logo: '/images/AzureLogo.png',
        mobileLogo: '/images/AzureLogo.png',
      }),
    }),
    new WebpackCodeAppender('bg7drig2n4'),
    new WebpackPwaManifest({
      name: 'spring-initializr',
      short_name: 'Start',
      description: `Initializr generates spring boot project with just what you need to start quickly!`,
      background_color: '#6db33f',
      inject: true,
      fingerprints: true,
      ios: true,
      start_url: '/',
      crossorigin: null,
      icons: [
        {
          src: path.resolve('src/images/initializr-icon.png'),
          sizes: [48, 72, 96, 144, 192, 256, 384, 512],
        },
      ],
    }),
  ],
}

module.exports = config
