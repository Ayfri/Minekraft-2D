const webpack = require('webpack');

config.plugins.push(
	new webpack.ProvidePlugin({
		PIXI: 'pixi.js'
	})
);
