/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.badlogicgames.superjumper

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3

class HighscoresScreen(internal var game: SuperJumper): ScreenAdapter() {
    internal var guiCam: OrthographicCamera
    internal var backBounds: Rectangle
    internal var touchPoint: Vector3
    internal var highScores: Array<String?>
    internal var xOffset = 0f
    internal var glyphLayout = GlyphLayout()
    internal val assets = game.assets!!

    init {
        guiCam = OrthographicCamera(320f, 480f)
        guiCam.position.set((320 / 2).toFloat(), (480 / 2).toFloat(), 0f)
        backBounds = Rectangle(0f, 0f, 64f, 64f)
        touchPoint = Vector3()
        highScores = arrayOfNulls(5)
        for (i in 0..4) {
            highScores[i] = (i + 1).toString() + ". " + Settings.highscores[i]
            glyphLayout.setText(assets.font, highScores[i])
            xOffset = Math.max(glyphLayout.width, xOffset)
        }
        xOffset = 160 - xOffset / 2 + assets.font.spaceWidth / 2
    }

    fun update() {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))

            if (backBounds.contains(touchPoint.x, touchPoint.y)) {
                Assets.playSound(assets.clickSound)
                game.screen = MainMenuScreen(game)
                return
            }
        }
    }

    fun draw() {
        val gl = Gdx.gl
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        guiCam.update()

        game.batcher.projectionMatrix = guiCam.combined
        game.batcher.disableBlending()
        game.batcher.begin()
        game.batcher.draw(assets.backgroundRegion, 0f, 0f, 320f, 480f)
        game.batcher.end()

        game.batcher.enableBlending()
        game.batcher.begin()
        game.batcher.draw(assets.highScoresRegion, 10f, (360 - 16).toFloat(), 300f, 33f)

        var y = 230f
        for (i in 4 downTo 0) {
            assets.font.draw(game.batcher, highScores[i], xOffset, y)
            y += assets.font.lineHeight
        }

        game.batcher.draw(assets.arrow, 0f, 0f, 64f, 64f)
        game.batcher.end()
    }

    override fun render(delta: Float) {
        update()
        draw()
    }
}
