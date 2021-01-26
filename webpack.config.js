const path = require('path');
const webpack = require('webpack');
const CopyPlugin = require('copy-webpack-plugin');
const { VueLoaderPlugin } = require('vue-loader');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = {
    mode: 'production',
    entry: './src/main/Vue/controller.js',
    output: {
        path: path.resolve(__dirname, 'src/main/resources/static'),
        publicPath: '/',
        filename: 'controller.js'
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: [{
                    loader: MiniCssExtractPlugin.loader
                }, 'css-loader' ]
            },
            {
                test: /\.vue$/,
                loader: 'vue-loader'
            }
        ]
    },
    plugins: [
        new VueLoaderPlugin(),
        new CopyPlugin({
            patterns: [
                {
                    from: "./node_modules/node-forge/dist/forge.all.min.js",
                    to: "./forge/"
                },
                {
                    from: "./node_modules/node-forge/dist/prime.worker.min.js",
                    to: "./forge/prime.worker.js",
                    toType: 'file',
                }
            ]
        }),
        new MiniCssExtractPlugin({
            filename: "[name].css"
        })
    ]
};

if (process.env.NODE_ENV === 'production') {
    module.exports.plugins = (module.exports.plugins || []).concat([
        new webpack.optimize.UglifyJsPlugin({})])}