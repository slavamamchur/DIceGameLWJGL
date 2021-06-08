package com.sadgames.gl3dengine.glrender.scene.objects

import com.sadgames.gl3dengine.glrender.scene.shaders.VBOShaderProgram
import java.util.*
import kotlin.math.sqrt

open class SpherePrimitiveObject @JvmOverloads constructor(
    textureResName: String?,
    program: VBOShaderProgram?,
    mass: Float,
    tag: Int,
    protected var radius: Float,
    mRefineLevel: Int = 2): GameItemObject(textureResName, program, mass, tag) {

    protected var _mPosX: Float
    protected var _mPosY: Float
    protected var _mPosZ: Float
    protected lateinit var _mVertices: ArrayList<Float>
    protected lateinit var _mIndices: ArrayList<Int>
    protected var _mCountVertex = 0
    protected var _mCountIndex = 0
    protected var _mCountTriangle = 0

    init {
        _mPosZ = 0f
        _mPosY = _mPosZ
        _mPosX = _mPosY

        CreateIcosphere(mRefineLevel)
    }

    private fun getMiddlePoint(p1: Int, p2: Int): Int {
        val point1 = FloatArray(3)
        val point2 = FloatArray(3)
        val middle = FloatArray(3)

        //Given the index of each vertex, we can get component x,y,z
        //by adding an offset (0,1,2)
        point1[0] = _mVertices[p1 * 3]
        point1[1] = _mVertices[p1 * 3 + 1]
        point1[2] = _mVertices[p1 * 3 + 2]
        point2[0] = _mVertices[p2 * 3]
        point2[1] = _mVertices[p2 * 3 + 1]
        point2[2] = _mVertices[p2 * 3 + 2]
        middle[0] = (point1[0] + point2[0]) / 2.0f
        middle[1] = (point1[1] + point2[1]) / 2.0f
        middle[2] = (point1[2] + point2[2]) / 2.0f

        //Normalize the new vertex to make sure it's on a unit sphere
        val len = len(middle[0], middle[1], middle[2])
        middle[0] /= len
        middle[1] /= len
        middle[2] /= len
        _mVertices.add(middle[0])
        _mVertices.add(middle[1])
        _mVertices.add(middle[2])
        _mCountVertex++

        return _mCountVertex - 1
    }

    private fun CreateIcosphere(refineLevel: Int) {
        //Start by creating the 12 vertices of an icosahedron
        //Phi is the golden ratio
        //http://paulbourke.net/geometry/platonic/
        val phi = ((1.0f + Math.sqrt(5.0)) / 2.0f).toFloat()
        _mCountVertex = 12
        _mVertices = ArrayList()
        val plen = len(-1f, phi, 0f)

        _mVertices.add(-1f / plen)
        _mVertices.add(phi / plen)
        _mVertices.add(0f) //v0
        _mVertices.add(1f / plen)
        _mVertices.add(phi / plen)
        _mVertices.add(0f) //v1
        _mVertices.add(-1f / plen)
        _mVertices.add(-phi / plen)
        _mVertices.add(0f) //v2
        _mVertices.add(1f / plen)
        _mVertices.add(-phi / plen)
        _mVertices.add(0f) //v3
        _mVertices.add(0f)
        _mVertices.add(-1f / plen)
        _mVertices.add(phi / plen) //v4
        _mVertices.add(0f)
        _mVertices.add(1f / plen)
        _mVertices.add(phi / plen) //v5
        _mVertices.add(0f)
        _mVertices.add(-1f / plen)
        _mVertices.add(-phi / plen) //v6
        _mVertices.add(0f)
        _mVertices.add(1f / plen)
        _mVertices.add(-phi / plen) //v7
        _mVertices.add(phi / plen)
        _mVertices.add(0f)
        _mVertices.add(-1f / plen) //v8
        _mVertices.add(phi / plen)
        _mVertices.add(0f)
        _mVertices.add(1f / plen) //v9
        _mVertices.add(-phi / plen)
        _mVertices.add(0f)
        _mVertices.add(-1f / plen) //v10
        _mVertices.add(-phi / plen)
        _mVertices.add(0f)
        _mVertices.add(1f / plen) //v11

        //then create the 20 triangles of the icosahedron (indices)
        //var faces = new List<TriangleIndices>();
        _mIndices = ArrayList()
        _mCountTriangle = 20
        _mCountIndex = 20 * 3

        //5 faces around point 0
        _mIndices.add(0)
        _mIndices.add(11)
        _mIndices.add(5)
        _mIndices.add(0)
        _mIndices.add(5)
        _mIndices.add(1)
        _mIndices.add(0)
        _mIndices.add(1)
        _mIndices.add(7)
        _mIndices.add(0)
        _mIndices.add(7)
        _mIndices.add(10)
        _mIndices.add(0)
        _mIndices.add(10)
        _mIndices.add(11)

        //5 adjacent faces
        _mIndices.add(1)
        _mIndices.add(5)
        _mIndices.add(9)
        _mIndices.add(5)
        _mIndices.add(11)
        _mIndices.add(4)
        _mIndices.add(11)
        _mIndices.add(10)
        _mIndices.add(2)
        _mIndices.add(10)
        _mIndices.add(7)
        _mIndices.add(6)
        _mIndices.add(7)
        _mIndices.add(1)
        _mIndices.add(8)

        //5 faces around point 3
        _mIndices.add(3)
        _mIndices.add(9)
        _mIndices.add(4)
        _mIndices.add(3)
        _mIndices.add(4)
        _mIndices.add(2)
        _mIndices.add(3)
        _mIndices.add(2)
        _mIndices.add(6)
        _mIndices.add(3)
        _mIndices.add(6)
        _mIndices.add(8)
        _mIndices.add(3)
        _mIndices.add(8)
        _mIndices.add(9)

        //5 adjacent faces
        _mIndices.add(4)
        _mIndices.add(9)
        _mIndices.add(5)
        _mIndices.add(2)
        _mIndices.add(4)
        _mIndices.add(11)
        _mIndices.add(6)
        _mIndices.add(2)
        _mIndices.add(10)
        _mIndices.add(8)
        _mIndices.add(6)
        _mIndices.add(7)
        _mIndices.add(9)
        _mIndices.add(8)
        _mIndices.add(1)

        //Refine triangles
        var currentTriCount = _mCountTriangle
        for (r in 0 until refineLevel) {
            val indicesTemp: ArrayList<Int> = ArrayList()
            var t = 0
            var i = 0

            while (t < currentTriCount) {
                //replace triangle by 4 triangles
                val a = getMiddlePoint(_mIndices.get(i), _mIndices.get(i + 1))
                val b = getMiddlePoint(_mIndices.get(i + 1), _mIndices.get(i + 2))
                val c = getMiddlePoint(_mIndices.get(i + 2), _mIndices.get(i))

                //Add the new indices
                //T1
                indicesTemp.add(_mIndices.get(i))
                indicesTemp.add(a)
                indicesTemp.add(c)
                //T2
                indicesTemp.add(_mIndices.get(i + 1))
                indicesTemp.add(b)
                indicesTemp.add(a)
                //T3
                indicesTemp.add(_mIndices.get(i + 2))
                indicesTemp.add(c)
                indicesTemp.add(b)
                //T4
                indicesTemp.add(a)
                indicesTemp.add(b)
                indicesTemp.add(c)
                t++
                i += 3
            }

            //replace _mIndices with indicesTemp to keep the new index array
            _mIndices = indicesTemp

            //update triangle & indices count
            _mCountTriangle *= 4
            _mCountIndex *= 4
            currentTriCount = _mCountTriangle
        }
    }

    override fun getVertexesArray(): FloatArray {
        val a = FloatArray(_mVertices.size)

        for (i in _mVertices.indices)
            a[i] = _mVertices[i] * radius

        return a
    }

    override fun getNormalsArray(): FloatArray {
        val a = FloatArray(_mVertices.size)

        for (i in _mVertices.indices)
            a[i] = -_mVertices[i]

        return a
    }

    override fun getFacesArray(): ShortArray {
        val a = ShortArray(_mIndices.size)

        for (i in _mIndices.indices)
            a[i] = _mIndices[i].toShort()

        return a
    }

    override fun getFacesCount() = _mIndices.size
}

private fun len(x: Float, y: Float, z: Float) = sqrt(x * x + y * y + (z * z))
