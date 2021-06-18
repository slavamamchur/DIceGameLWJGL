local gameLogic, gameScene = ...

local ROLLING_DICE_SOUND = 'rolling_dice.mp3'
local SKY_BOX_CUBE_MAP_OBJECT = 'SKY_BOX_CUBE_MAP_OBJECT'
local TERRAIN_MESH_OBJECT = 'TERRAIN_MESH_OBJECT'
local WATER_MESH_OBJECT = "WATER_MESH_OBJECT"
local CHIP_MESH_OBJECT = 'CHIP_MESH_OBJECT'
local DICE_MESH_OBJECT = '5bae5fc2f2675fb54e7cb7f5'
local WINGS_MESH_OBJECT = 'WP_FLY_FORWARD'
local EXIT_MESH_OBJECT = '5bb64b8718e775d89e163320'
local BACK_MESH_OBJECT = '5bb6671fea957bd2affb428c'
local SKIP_MESH_OBJECT = '5bb7042244dd519c21662955'
local MORE_MESH_OBJECT = '5bb7785f44dd519c21662956'

local TERRAIN_OBJECT_TYPE = 1
local CHIP_DEFAULT_WEIGHT = 1.0
local COLLISION_OBJECT = 1
local TERRAIN_MATERIAL = 1
local DEFAULT_TEXTURE_SIZE = 500
local GAME_STATE_WAIT = 0
local POINT_TYPE_FINISH = 6
local CHIP_ANIMATION_DURATION = 500
local PATH_COLOR = -16711936
local WAY_POINT_COLOR = -65536
local LAND_SIZE_IN_WORLD_SPACE = 10.0
local DEFAULT_CAMERA_X, DEFAULT_CAMERA_Y, DEFAULT_CAMERA_Z = 0.0, 3.0, 3.0
local DEFAULT_CAMERA_PITCH, DEFAULT_CAMERA_YAW, DEFAULT_CAMERA_ROLL = 45.0, 0.0, 0.0
local ROTATE_BY_X = 1
local ROTATE_BY_Y = 2
local ROTATE_BY_Z = 4

local MOVE_SKIP = 2
local MOVE_MORE = 3
local FLY_BACK = 4
local FLY_FORWARD = 5
local FINISH = 6

local DICE_FACES_VALUES = {68, 85, 17, 0, 51, 34}

local ON_PLAY_TURN_ANIMATION_END = 'rollDice'
local ON_STOP_MOVING_ANIMATION_END = 'playerNextMove'
local ON_PLAYER_CONTINUE_TURN_EVENT_HANDLER = 'onPlayerContinueTurn'

onCameraInit  = function(defCam)
    local camera

    if gameLogic:iGetSettingsManager():isIn_2D_Mode() == true then
        camera = gameScene:createCam2D(LAND_SIZE_IN_WORLD_SPACE)
    else
        camera = gameScene:createCamIsometric(DEFAULT_CAMERA_X,
                                                             DEFAULT_CAMERA_Y,
                                                             DEFAULT_CAMERA_Z,
                                                             DEFAULT_CAMERA_PITCH,
                                                             DEFAULT_CAMERA_YAW,
                                                             DEFAULT_CAMERA_ROLL)
        camera:rotateX(22.5)
    end

    gameScene:setCamera(camera)
end

function playSound(name)
    local sound = stopSound(name)

    if not (sound == nil) then
        sound:play()
    end
end

function stopSound(name)
    local sound = gameLogic:getSoundObject(name)

    if not (sound == nil) then
        sound:stop()
    end

    return sound
end

onRollingObjectStart = function(gameObject)
    if gameObject == gameScene:getObject(DICE_MESH_OBJECT) then
        playSound(ROLLING_DICE_SOUND)
    end
end

onRollingObjectStop = function(gameObject)
    stopSound(ROLLING_DICE_SOUND)
end

function startWith(item, pattern)
    local i, j = string.find(item:getItemName(), pattern)
    return i == 1
end

function findFirst(pattern)
    local list = gameScene:getObject(TERRAIN_MESH_OBJECT):getChilds():values():iterator()

    while list:hasNext() do
        local item = list:next()

        if startWith(item, pattern) then
            return item
        end
    end

    return nil
end

function chnageWayPointsVisibility()
    local visible = not gameLogic:iGetSettingsManager():isIn_2D_Mode()
    local list = gameScene:getObject(TERRAIN_MESH_OBJECT):getChilds():values():iterator()

    gameScene:getObject(WATER_MESH_OBJECT):setVisible(visible)

    while list:hasNext() do
        local item = list:next()

        if startWith(item, 'WP_') then
            item:setVisible(visible)
        end
    end
end

onMovingObjectStop = function(gameObject, gameInstance)
    if not (gameInstance == nil) and (gameObject == gameScene:getObject(DICE_MESH_OBJECT)) then
        gameInstance:setStepsToGo(getTopFaceDiceValue(gameObject))
        gameLogic:getRestApiWrapper():showTurnInfo(gameInstance)

        gameScene:restorePrevViewMode()

        gameObject:hideObject()
        chnageWayPointsVisibility()

        gameScene:setZoomCameraAnimation(gameScene:createZoomCameraAnimation(2.0))
        gameScene:getZoomCameraAnimation():startAnimation(nil, ON_STOP_MOVING_ANIMATION_END, { gameInstance})
    end
end

beforeDrawFrame = function(frametime)
    local skyBox = gameScene:getObject(SKY_BOX_CUBE_MAP_OBJECT)

    if not (skyBox == nil) then
        skyBox:calcRotationAngle(frametime)
        gameScene:getCachedShaderLua(TERRAIN_OBJECT_TYPE):setSkyBoxRotationAngle(-skyBox:getRotationAngle())

    end
end

onCreateReflectionMap = function(reflectMap, refractMap)
    gameScene:getObject(WATER_MESH_OBJECT):setWaterReflectionMap(reflectMap:getFboTexture())
    gameScene:getObject(WATER_MESH_OBJECT):setGlCubeMap(refractMap:getFboTexture())
end

onPlayTurn = function()
    gameScene:setZoomCameraAnimation(gameScene:createZoomCameraAnimation(0.5))
    gameScene:getZoomCameraAnimation():startAnimation(nil, ON_PLAY_TURN_ANIMATION_END, {})
end

drawPath = function(blendMap, gameEntity)
    if gameEntity:isDrawGamePoints() then
        local scaleFactor = blendMap:getWidth() * 1.0 / DEFAULT_TEXTURE_SIZE --todo: use scaled size ???
        local canvas = blendMap:getGraphics()

--[[        canvas:setColor(gameScene:getColor(1.0, 1.0, 1.0, 1.0))
        canvas:fillRect(0, 0, blendMap:getWidth(), blendMap:getHeight())]]

        for i = 0, gameEntity:getGamePoints():size() - 2 do
            canvas:setColor(gameScene:getColor(0, 1.0, 0, 1.0))
            local fromPt = gameEntity:getGamePoints():get(i):asVector2fLua(scaleFactor)
            local toPt = gameEntity:getGamePoints():get(i + 1):asVector2fLua(scaleFactor)
            canvas:drawLine(fromPt.x, fromPt.y, toPt.x, toPt.y)
            canvas:setColor(gameScene:getColor(0, 0.55, 0, 1.0))
            canvas:drawLine(fromPt.x, fromPt.y - 1, toPt.x, toPt.y - 1)
            canvas:drawLine(fromPt.x, fromPt.y + 1, toPt.x, toPt.y + 1)

            canvas:setColor(gameScene:getColor(0, 0.25, 0, 1.0))
            canvas:drawLine(fromPt.x, fromPt.y - 2, toPt.x, toPt.y - 2)
            canvas:drawLine(fromPt.x, fromPt.y + 2, toPt.x, toPt.y + 2)

            canvas:setColor(gameScene:getColor(0, 0.125, 0, 1.0))
            canvas:drawLine(fromPt.x, fromPt.y - 3, toPt.x, toPt.y - 3)
            canvas:drawLine(fromPt.x, fromPt.y + 3, toPt.x, toPt.y + 3)

            canvas:setColor(gameScene:getColor(0, 0.0625, 0, 1.0))
            canvas:drawLine(fromPt.x, fromPt.y - 4, toPt.x, toPt.y - 4)
            canvas:drawLine(fromPt.x, fromPt.y + 4, toPt.x, toPt.y + 4)

            canvas:setColor(gameScene:getColor(0, 0.03125, 0, 1.0))
            canvas:drawLine(fromPt.x, fromPt.y - 5, toPt.x, toPt.y - 5)
            canvas:drawLine(fromPt.x, fromPt.y + 5, toPt.x, toPt.y + 5)

            canvas:setColor(gameScene:getColor(0 , 0, 1.0, 1.0))
            canvas:fillOval(fromPt.x, fromPt.y, 7.5 * scaleFactor, 7.5 * scaleFactor)
            if i == (gameEntity:getGamePoints():size() - 2) then
                canvas:fillOval(toPt.x, toPt.y, 7.5 * scaleFactor, 7.5 * scaleFactor)
            end

--[[            for k = 0, 2 do
                blendMap:drawCircle(fromPt.x, fromPt.y, 7.5 * scaleFactor - k) --todo: use drawImage()
                if i == (gameEntity:getGamePoints():size() - 2) then
                    blendMap:drawCircle(toPt.x, toPt.y, 7.5 * scaleFactor - k)
                end
            end]]
        end
    end
end

rollDice = function()
    local dice = gameScene:getObject(DICE_MESH_OBJECT)

    gameScene:switchTo2DMode()
    chnageWayPointsVisibility()

    dice:createRigidBody()
    dice:setPWorldTransform(generateDiceInitialTransform())
    dice:get_body():setLinearVelocity(generateForceVector())

    dice:showObject()
    gameScene:getPhysicalWorldObject():addRigidBody(dice:get_body())
end

playerNextMove = function(gameInstance)
    gameLogic:getRestApiWrapper():moveGameInstance(gameInstance, gameLogic:getContinuation(ON_PLAYER_CONTINUE_TURN_EVENT_HANDLER))

    --gameLogic:setGameInstanceEntity(gameInstance)
    --gameLogic:onPlayerContinueTurn()
end

onPlayerContinueTurn = function(gameInstance)
    --todo: (update OGL UI), split 2 move and after move (in Continuation call lua)
    gameLogic:setGameInstanceEntity(gameInstance)
    gameLogic:onPlayerContinueTurn()
end

onDiceObjectInit = function(gameObject)
    gameObject:hideObject()
end

onPlayerMakeTurn = function(gameInstanceEntity, savedPlayers, delegate)
    local playersOnWayPoints = {}
    for i = 1, gameInstanceEntity:getGame():getGamePoints():size() do
        table.insert(playersOnWayPoints, i, 0)
    end

    local movedPlayerIndex = -1

    for i = 0, gameInstanceEntity:getPlayers():size() - 1 do
        local player = gameInstanceEntity:getPlayers():get(i)
        playersOnWayPoints[player:getCurrentPoint() + 1] = playersOnWayPoints[player:getCurrentPoint() + 1] + 1
    end

    local endGamePoint
    local playersCnt = 0

    if not (savedPlayers == nil) then
        for i = 0, gameInstanceEntity:getPlayers():size() - 1 do
            local currentPointIdx = gameInstanceEntity:getPlayers():get(i):getCurrentPoint()

            if not (savedPlayers[i + 1]:getCurrentPoint() == currentPointIdx) then
                movedPlayerIndex = i
                endGamePoint = gameInstanceEntity:getGame():getGamePoints():get(currentPointIdx)
                playersCnt = playersOnWayPoints[currentPointIdx + 1] - 1

                break
            end
        end
    end

    if movedPlayerIndex >= 0 then
        animateChip(gameInstanceEntity, delegate, endGamePoint, playersCnt,
                    gameScene:getObject(string.format(
                                                                    '%s_%s',
                                                                     CHIP_MESH_OBJECT,
                                                                     savedPlayers[movedPlayerIndex + 1]:getName())))
    else
        playerNextMove(gameInstanceEntity)
    end
end

onGameRestarted = function(gameInstanceEntity)
    --todo: update OGL UI

    gameLogic:updateSavedPlayers()

    gameInstanceEntity:setStateLua(GAME_STATE_WAIT)
    gameInstanceEntity:setCurrentPlayer(0)
    gameInstanceEntity:setStepsToGo(0)

    for i = 0, gameInstanceEntity:getPlayers():size() - 1 do
        local player = gameInstanceEntity:getPlayers():get(i)
        player:setCurrentPoint(0)
        player:setFinished(false)
        player:setSkipped(false)
    end

    pcall(moveChips, gameInstanceEntity)
end

--todo: after finish callback

animateChip = function(gameInstanceEntity, delegate, endGamePoint, playersCnt, chip)
    if playersCnt < 0 then
        playersCnt = 0
    end

    local chipPlace = getChipPlace(endGamePoint,
                                   playersCnt,
                           (gameInstanceEntity:getStepsToGo() == 0) or (endGamePoint:getType():ordinal() == POINT_TYPE_FINISH))
    local move = gameScene:createTranslateAnimation(chip:getPlace().x, chipPlace.x,
                                                                   0.0, 0.0,
                                                                   chip:getPlace().y, chipPlace.y,
                                                                   CHIP_ANIMATION_DURATION)
    chip:setPlace(chipPlace)
    chip:setAnimation(move)
    move:startAnimation(chip, delegate)
end

moveChips = function(gameInstanceEntity)
    local playersOnWayPoints = {}
    for i = 1, gameInstanceEntity:getGame():getGamePoints():size() do
        table.insert(playersOnWayPoints, i, 0)
    end

    for i = 0, gameInstanceEntity:getPlayers():size() - 1 do
        local player = gameInstanceEntity:getPlayers():get(i)
        local chip = gameScene:getObject(string.format('%s_%s', CHIP_MESH_OBJECT, player:getName()))
        local currentPointIdx = player:getCurrentPoint() + 1

        playersOnWayPoints[currentPointIdx] = playersOnWayPoints[currentPointIdx] + 1
        chip:setInWorldPosition(getChipPlace(gameInstanceEntity:getGame():getGamePoints():get(player:getCurrentPoint()),
                                  playersOnWayPoints[currentPointIdx] - 1,
                                     true))
    end
end

function createSpinAnimation(rotationAxe)
    local spin = gameScene:createRotateAnimation(-360.0, rotationAxe, 4000)
    spin:setRepeatCount(0)

    return spin
end

function loadObject(object, pattern)
    local prev = findFirst(pattern)

    if prev == nil then
        object:loadObject()
    else
        object:loadFromObject(prev)
    end
end

function createWPFlyForward(gameEntity)
    local wp = gameEntity:createNewItem(WINGS_MESH_OBJECT,
            TERRAIN_MESH_OBJECT,
            CHIP_DEFAULT_WEIGHT,
            COLLISION_OBJECT,
            TERRAIN_MATERIAL):createSceneObjectLua(gameScene, 0xFFFFFFFF)
    wp:setInitialScale(0.015625)
    wp:setInitialTranslation(0.0, 0.0, 0.25)
    loadObject(wp, 'WP_FLY_FORWARD_')
    wp:setRotationX(-90)
    wp:setAnimation(createSpinAnimation(ROTATE_BY_Z));
    wp:setItemName('WP_FLY_FORWARD_')

    return wp
end

function createWPFinish(gameEntity)
    local wp = gameEntity:createNewItem(EXIT_MESH_OBJECT,
    TERRAIN_MESH_OBJECT,
    CHIP_DEFAULT_WEIGHT,
    COLLISION_OBJECT,
    TERRAIN_MATERIAL):createSceneObject(gameScene)

    wp:setInitialScale(0.5)
    wp:setInitialTranslation(0.0, 0.33, 0.0)
    loadObject(wp, 'WP_FINISH_')
    wp:setAnimation(createSpinAnimation(ROTATE_BY_Y));
    wp:setItemName('WP_FINISH_')

    return wp
end

function createWPFlyBack(gameEntity)
    local wp = gameEntity:createNewItem(BACK_MESH_OBJECT,
    TERRAIN_MESH_OBJECT,
    CHIP_DEFAULT_WEIGHT,
    COLLISION_OBJECT,
    TERRAIN_MATERIAL):createSceneObject(gameScene)

    wp:setInitialScale(0.1)
    wp:setInitialTranslation(0.0, 0.33, 0.0)
    loadObject(wp, 'WP_FLY_BACK_')
    wp:setAnimation(createSpinAnimation(ROTATE_BY_Y));
    wp:setItemName('WP_FLY_BACK_')

    return wp
end

function createWPMoveSkip(gameEntity)
    local wp = gameEntity:createNewItem(SKIP_MESH_OBJECT,
    TERRAIN_MESH_OBJECT,
    CHIP_DEFAULT_WEIGHT,
    COLLISION_OBJECT,
    TERRAIN_MATERIAL):createSceneObject(gameScene)

    wp:setInitialScale(0.16667)
    wp:setTwoSidedSurface(true)
    loadObject(wp, 'WP_MOVE_SKIP_')
    wp:setAnimation(createSpinAnimation(ROTATE_BY_Y));
    wp:setItemName('WP_MOVE_SKIP_')

    return wp
end

function createWPMoveMore(gameEntity)
    local wp = gameEntity:createNewItem(MORE_MESH_OBJECT,
    TERRAIN_MESH_OBJECT,
    CHIP_DEFAULT_WEIGHT,
    COLLISION_OBJECT,
    TERRAIN_MATERIAL):createSceneObject(gameScene)

    wp:setInitialScale(0.00125)
    wp:setInitialTranslation(0.0, 0.0625, 0.0)
    loadObject(wp, 'WP_MOVE_MORE_')
    wp:setAnimation(createSpinAnimation(ROTATE_BY_Y));
    wp:setItemName('WP_MOVE_MORE_')

    return wp
end

function createSpecialPoint(type, number, place, gameEntity)
    local map = gameScene:getObject(TERRAIN_MESH_OBJECT)
    local scaleFactor = map:getGlTexture():getWidth() * 1.0 / DEFAULT_TEXTURE_SIZE
    local coords = map:map2WorldCoord(place.x * scaleFactor, place.y * scaleFactor)

    local switch = {
    [MOVE_SKIP] = function () return createWPMoveSkip(gameEntity) end,
    [MOVE_MORE] = function () return createWPMoveMore(gameEntity) end,
    [FLY_BACK] = function () return createWPFlyBack(gameEntity) end,
    [FLY_FORWARD] = function () return createWPFlyForward(gameEntity) end,
    [FINISH] = function () return createWPFinish(gameEntity) end
    }

    local wp = switch[type]()
    wp:setInWorldPosition(coords)
    wp:setItemName(string.format('%s%d', wp:getItemName(), number))
    wp:animationStart()

    map:putChild(wp, wp:getItemName())
end

onCreateDynamicItems = function(gameEntity, gameInstance)

    if (gameInstance == nil) or (gameEntity:getGamePoints() == nil) then
        return
    end

    local prevChip = nil
    local playersOnWayPoints = {}
    local wayPointsCounter = {0, 0, 0, 0, 0, 0, 0}

    for i = 1, gameEntity:getGamePoints():size() do
        table.insert(playersOnWayPoints, i, 0)

        local pType = gameEntity:getGamePoints():get(i-1):getType():ordinal()
        if pType > 1 then
            wayPointsCounter[pType + 1] = wayPointsCounter[pType + 1] + 1
            createSpecialPoint(pType,
            wayPointsCounter[pType + 1],
            gameEntity:getGamePoints():get(i-1):asVector2f(),
            gameEntity)
        end
    end

    for i = 0, gameInstance:getPlayers():size() - 1 do
        local player = gameInstance:getPlayers():get(i)
        local chip = gameEntity:createNewItem(CHIP_MESH_OBJECT,
        TERRAIN_MESH_OBJECT,
        CHIP_DEFAULT_WEIGHT,
        COLLISION_OBJECT,
        TERRAIN_MATERIAL)
        :createSceneObject(gameScene, player:getColor())

        chip:setInitialScale(0.2)
        chip:setInitialTranslation(0.0, 0.08, 0.0)
        chip:setTwoSidedSurface(false);
        chip:setItemName(string.format('%s_%s', CHIP_MESH_OBJECT, player:getName()))

        local parent = gameScene:getObject(TERRAIN_MESH_OBJECT)
        local currentPointIdx = player:getCurrentPoint() + 1
        playersOnWayPoints[currentPointIdx] = playersOnWayPoints[currentPointIdx] + 1
        chip:setInWorldPosition(getChipPlace(gameEntity:getGamePoints():get(player:getCurrentPoint()),
        playersOnWayPoints[currentPointIdx] - 1,
        true))

        if prevChip == nil then
            chip:loadObject()
        else
            chip:loadFromObject(prevChip)
        end

        parent:putChild(chip, chip:getItemName())
        prevChip = chip
    end
end

function getChipPlace(point, playersCnt, rotate)
    local map = gameScene:getObject(TERRAIN_MESH_OBJECT)
    local scaleFactor = map:getGlTexture():getWidth() * 1.0 / DEFAULT_TEXTURE_SIZE
    local toX2 = point:getxPos() * scaleFactor
    local toZ2 = point:getyPos() * scaleFactor

    if rotate then
        local angle = getChipRotationAngle(playersCnt)
        toX2 = toX2 - 7.5 * scaleFactor * math.sin(angle)
        toZ2 = toZ2 - 7.5 * scaleFactor * math.cos(angle)
    end

    return map:map2WorldCoord(toX2, toZ2)
end

function getChipRotationAngle(playersCnt)
    if playersCnt == 0 then
        return 0.0
    end

    local part = 8
    local b
    local angle

    repeat
        angle = 360.0 / part
        b = part - 1
        part = part / 2
    until not ((math.fmod(playersCnt, part) == 0) and not (part == 1))

    return math.rad((2 * playersCnt - b) * angle)
end

function getTopFaceDiceValue(dice)
    local result = 0
    local max_y = 0.0
    local normals = dice:getRaw3DModel():getNormalsLua()

    for i = 1, #normals / 3 do
        local normalVector = gameLogic:mulMV(dice:getModelMatrix(), { normals[i * 3 - 2], normals[i * 3 - 1], normals[i * 3], 1.0})

        if normalVector.y > max_y then
            max_y = normalVector.y;
            result = i - 1;
        end
    end

    local key = getKeyForValue(DICE_FACES_VALUES, result)
    if key == nil then
        key = 0
    end

    return key
end

function getKeyForValue(tbl, value)
    local result = nil

    for k, v in pairs(tbl) do
        if v == value then
            result = k
            break
        end
    end

    return result
end

function generateForceVector()
    math.randomseed(os.time())

    local fxz = 3.5 + math.random(0, 1) * 1.0 --2
    local fy = fxz * 3.0 / 4.0
    local fVector = {0.0, fy, -fxz, 1.0}

    local transform = gameScene:createTransform()
    transform:rotY(math.rad(45.0 - math.random(0, 90) * 1.0))

    return gameLogic:mulMV(transform, fVector)
end

function generateDiceInitialTransform()
    math.randomseed(os.time())

    local transformer = gameScene:createTransform()
    local transformingObject = gameScene:createTransform()

    transformingObject:setIdentity()
    transformer:setIdentity()

    transformer:setTranslation(gameScene:createVector3f(0.0, 0.5, 2.5))
    transformingObject:mul(transformer)

    transformer:rotX(math.rad(math.random(0, 3) * 90.0)) --4
    transformingObject:mul(transformer)

    transformer:rotY(math.rad(math.random(0, 3) * 90.0))
    transformingObject:mul(transformer)

    transformer:rotZ(math.rad(math.random(0, 3) * 90.0))
    transformingObject:mul(transformer)

    return transformingObject
end

function make_array(dataClass, values)
    local arrayClass = luajava.bindClass("java.lang.reflect.Array")
    if(arrayClass == nil) then
        print("Can't get array class")
        return nil
    end

    local newTypedArray = arrayClass:newInstance(dataClass, #values)
    if(newTypedArray == nil) then
        print("Can't get array class")
        return nil
    end

    for i=1,#values do
        arrayClass:set(newTypedArray, i-1, values[i])
    end

    return newTypedArray
end
