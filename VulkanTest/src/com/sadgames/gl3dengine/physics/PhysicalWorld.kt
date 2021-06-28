package com.sadgames.gl3dengine.physics

import com.bulletphysics.collision.broadphase.DbvtBroadphase
import com.bulletphysics.collision.dispatch.CollisionDispatcher
import com.bulletphysics.collision.dispatch.CollisionObject
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration
import com.bulletphysics.dynamics.DiscreteDynamicsWorld
import com.bulletphysics.dynamics.RigidBody
import com.bulletphysics.dynamics.RigidBodyConstructionInfo
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver
import com.bulletphysics.linearmath.DefaultMotionState
import com.bulletphysics.linearmath.Transform
import com.sadgames.gl3dengine.gamelogic.GameEventsCallbackInterface
import com.sadgames.gl3dengine.glrender.GLRenderConsts.DEFAULT_GRAVITY_VECTOR
import com.sadgames.gl3dengine.glrender.GdxExt
import com.sadgames.gl3dengine.glrender.scene.objects.GameItemObject
import com.sadgames.gl3dengine.glrender.scene.objects.PNodeObject
import java.util.*
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

object PhysicalWorld {

    var physicalWorld: DiscreteDynamicsWorld; private set

    private val gameEventsCallBackListener: GameEventsCallbackInterface?; get() = GdxExt.gameLogic
    private var oldNumContacts = 0
    private var old_time = System.currentTimeMillis()
    //private var old_frame_time = 0f

    init {
        val dispatcher = CollisionDispatcher(DefaultCollisionConfiguration())
        physicalWorld = DiscreteDynamicsWorld(dispatcher, DbvtBroadphase(), SequentialImpulseConstraintSolver(), dispatcher.collisionConfiguration)
        physicalWorld.setGravity(DEFAULT_GRAVITY_VECTOR)

        gameEventsCallBackListener?.onInitPhysics(physicalWorld)
    }

    fun simulateStep(time: Long, frameTime: Float) {
            //val realInterval = if (old_frame_time == 0f) 0f else (time - old_frame_time) / 1000f
            //old_frame_time = time

            physicalWorld.stepSimulation(frameTime, 0, frameTime)

            for (i in 0 until physicalWorld.dispatcher.numManifolds)
                if (physicalWorld.dispatcher.getManifoldByIndexInternal(i).numContacts > 0
                    && physicalWorld.dispatcher.getManifoldByIndexInternal(i).numContacts != oldNumContacts
                ) {
                    gameEventsCallBackListener?.onRollingObjectStart(
                        (physicalWorld.dispatcher.getManifoldByIndexInternal(
                            i
                        ).body1 as CollisionObject).userPointer as GameItemObject
                    )

                    oldNumContacts =
                        physicalWorld.dispatcher.getManifoldByIndexInternal(i).numContacts
                    old_time = time //=0
                } else //todo: error - too small interval to detect movement and unexpected stop !!! //old_time += frameTime
                    if (physicalWorld.dispatcher.getManifoldByIndexInternal(i).numContacts == 0 || (time - old_time > 150))
                        gameEventsCallBackListener?.onRollingObjectStop(null)
    }

    @JvmStatic fun createRigidBody(item: PNodeObject): RigidBody {
        val rnd = Random(System.currentTimeMillis())
        val bodyInertia = Vector3f()

        item._shape.calculateLocalInertia(item.mass, bodyInertia)

        val bodyCI = RigidBodyConstructionInfo(item.mass, DefaultMotionState(Transform(Matrix4f(item.modelMatrix))), item._shape, bodyInertia)
        bodyCI.restitution = 0.0125f + rnd.nextInt(125) * 1f / 10000f
        bodyCI.friction = 0.5f + rnd.nextInt(4) * 1f / 10f

        val result = RigidBody(bodyCI)
        result.userPointer = item

        return result
    }
}
